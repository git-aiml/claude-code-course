import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as ratingService from './ratingService'

// Mock global fetch
global.fetch = vi.fn()

describe('ratingService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    global.fetch.mockClear()
  })

  describe('submitRating', () => {
    it('should submit rating with correct parameters', async () => {
      const mockResponse = {
        songId: 1,
        artist: 'Test Artist',
        title: 'Test Song',
        thumbsUpCount: 1,
        thumbsDownCount: 0,
        userRating: 'THUMBS_UP',
        message: 'Rating submitted successfully',
      }

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await ratingService.submitRating(
        'Test Artist',
        'Test Song',
        'user-123',
        'THUMBS_UP',
        'ENGLISH'
      )

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/ratings',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            artist: 'Test Artist',
            title: 'Test Song',
            userId: 'user-123',
            ratingType: 'THUMBS_UP',
            stationCode: 'ENGLISH',
          }),
        }
      )

      expect(result).toEqual(mockResponse)
    })

    it('should handle THUMBS_DOWN rating', async () => {
      const mockResponse = {
        songId: 1,
        artist: 'Test Artist',
        title: 'Test Song',
        thumbsUpCount: 0,
        thumbsDownCount: 1,
        userRating: 'THUMBS_DOWN',
        message: 'Rating submitted successfully',
      }

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await ratingService.submitRating(
        'Test Artist',
        'Test Song',
        'user-123',
        'THUMBS_DOWN',
        'ENGLISH'
      )

      expect(result.userRating).toBe('THUMBS_DOWN')
      expect(result.thumbsDownCount).toBe(1)
    })

    it('should throw error when response is not ok', async () => {
      const errorMessage = 'Rate limit exceeded'

      global.fetch.mockResolvedValue({
        ok: false,
        json: async () => ({ message: errorMessage }),
      })

      await expect(
        ratingService.submitRating('Artist', 'Title', 'user-123', 'THUMBS_UP', 'ENGLISH')
      ).rejects.toThrow(errorMessage)
    })

    it('should throw error with status text if no message provided', async () => {
      global.fetch.mockResolvedValue({
        ok: false,
        statusText: 'Internal Server Error',
        json: async () => ({}),
      })

      await expect(
        ratingService.submitRating('Artist', 'Title', 'user-123', 'THUMBS_UP', 'ENGLISH')
      ).rejects.toThrow('Failed to submit rating: Internal Server Error')
    })

    it('should handle network errors', async () => {
      const networkError = new Error('Network connection failed')
      global.fetch.mockRejectedValue(networkError)

      await expect(
        ratingService.submitRating('Artist', 'Title', 'user-123', 'THUMBS_UP', 'ENGLISH')
      ).rejects.toThrow('Network connection failed')
    })
  })

  describe('getRatingCounts', () => {
    it('should get rating counts with required parameters', async () => {
      const mockResponse = {
        songId: 1,
        artist: 'Test Artist',
        title: 'Test Song',
        thumbsUpCount: 42,
        thumbsDownCount: 5,
        userRating: null,
      }

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await ratingService.getRatingCounts(
        'Test Artist',
        'Test Song',
        'user-123',
        'ENGLISH'
      )

      const callUrl = global.fetch.mock.calls[0][0]
      expect(callUrl).toContain('/api/ratings/counts')
      expect(callUrl).toContain('artist=')
      expect(callUrl).toContain('title=')
      expect(callUrl).toContain('stationCode=ENGLISH')

      expect(result).toEqual(mockResponse)
    })

    it('should include userId in query params when provided', async () => {
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ thumbsUpCount: 0, thumbsDownCount: 0 }),
      })

      await ratingService.getRatingCounts(
        'Artist',
        'Title',
        'user-123',
        'ENGLISH'
      )

      const callUrl = global.fetch.mock.calls[0][0]
      expect(callUrl).toContain('userId=user-123') // May be encoded as + or %20
    })

    it('should not include userId when not provided', async () => {
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ thumbsUpCount: 0, thumbsDownCount: 0 }),
      })

      await ratingService.getRatingCounts(
        'Artist',
        'Title',
        null,
        'ENGLISH'
      )

      const callUrl = global.fetch.mock.calls[0][0]
      expect(callUrl).not.toContain('userId')
    })

    it('should include artist and title in query params', async () => {
      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => ({ thumbsUpCount: 0, thumbsDownCount: 0 }),
      })

      await ratingService.getRatingCounts(
        'Test Artist',
        'Test Song',
        'user-123',
        'ENGLISH'
      )

      const callUrl = global.fetch.mock.calls[0][0]
      // URLSearchParams uses + for spaces, not %20
      expect(callUrl).toContain('artist=')
      expect(callUrl).toContain('Artist')
      expect(callUrl).toContain('title=')
      expect(callUrl).toContain('Song')
      expect(callUrl).toContain('stationCode=ENGLISH')
    })

    it('should throw error when response is not ok', async () => {
      global.fetch.mockResolvedValue({
        ok: false,
        statusText: 'Not Found',
      })

      await expect(
        ratingService.getRatingCounts('Artist', 'Title', 'user-123', 'ENGLISH')
      ).rejects.toThrow('Failed to get rating counts: Not Found')
    })

    it('should handle network errors', async () => {
      const networkError = new Error('Network timeout')
      global.fetch.mockRejectedValue(networkError)

      await expect(
        ratingService.getRatingCounts('Artist', 'Title', 'user-123', 'ENGLISH')
      ).rejects.toThrow('Network timeout')
    })

    it('should return counts with zero values for new songs', async () => {
      const mockResponse = {
        songId: null,
        artist: 'Unknown Artist',
        title: 'Unknown Song',
        thumbsUpCount: 0,
        thumbsDownCount: 0,
        userRating: null,
      }

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      })

      const result = await ratingService.getRatingCounts(
        'Unknown Artist',
        'Unknown Song',
        'user-123',
        'ENGLISH'
      )

      expect(result.thumbsUpCount).toBe(0)
      expect(result.thumbsDownCount).toBe(0)
      expect(result.userRating).toBeNull()
    })
  })
})
