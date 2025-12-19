import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import SongRating from './SongRating'
import * as ratingService from '../services/ratingService'
import { StationProvider } from '../contexts/StationContext'

// Mock the ratingService
vi.mock('../services/ratingService')

// Mock the userIdentity utility
vi.mock('../utils/userIdentity', () => ({
  getUserId: vi.fn(() => 'test-user-id'),
}))

describe('SongRating Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  const defaultStationContext = {
    currentStation: { code: 'ENGLISH', name: 'RadioAwa English' },
  }

  const renderWithStation = (component, station = defaultStationContext) => {
    // Create a mock provider
    const MockProvider = ({ children }) => {
      return children
    }

    return render(
      <div>{component}</div>
    )
  }

  it('should display loading state initially', async () => {
    ratingService.getRatingCounts.mockImplementation(() =>
      new Promise(resolve => setTimeout(() => resolve({
        thumbsUpCount: 0,
        thumbsDownCount: 0,
        userRating: null,
      }), 100))
    )

    render(
      <SongRating artist="Test Artist" title="Test Song" />,
      { wrapper: ({ children }) => <div>{children}</div> }
    )

    expect(screen.getByText('Loading ratings...')).toBeInTheDocument()
  })

  it('should display thumbs up and down buttons', async () => {
    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByLabelText('Thumbs up')).toBeInTheDocument()
      expect(screen.getByLabelText('Thumbs down')).toBeInTheDocument()
    })
  })

  it('should display rating counts', async () => {
    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 42,
      thumbsDownCount: 5,
      userRating: null,
    })

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByText('42')).toBeInTheDocument()
      expect(screen.getByText('5')).toBeInTheDocument()
    })
  })

  it('should highlight user rating when already voted', async () => {
    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 42,
      thumbsDownCount: 5,
      userRating: 'THUMBS_UP',
    })

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      const thumbsUpButton = screen.getByLabelText('Thumbs up')
      expect(thumbsUpButton).toHaveClass('active')
    })
  })

  it('should submit rating on button click', async () => {
    const user = userEvent.setup()

    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    ratingService.submitRating.mockResolvedValue({
      thumbsUpCount: 11,
      thumbsDownCount: 5,
      userRating: 'THUMBS_UP',
    })

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByLabelText('Thumbs up')).toBeInTheDocument()
    })

    const thumbsUpButton = screen.getByLabelText('Thumbs up')
    await user.click(thumbsUpButton)

    await waitFor(() => {
      expect(ratingService.submitRating).toHaveBeenCalledWith(
        'Test Artist',
        'Test Song',
        'test-user-id',
        'THUMBS_UP',
        expect.any(String)
      )
    })
  })

  it('should disable buttons while submitting', async () => {
    const user = userEvent.setup()

    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    ratingService.submitRating.mockImplementation(
      () => new Promise(resolve => setTimeout(() => resolve({
        thumbsUpCount: 11,
        thumbsDownCount: 5,
        userRating: 'THUMBS_UP',
      }), 100))
    )

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByLabelText('Thumbs up')).toBeInTheDocument()
    })

    const thumbsUpButton = screen.getByLabelText('Thumbs up')
    await user.click(thumbsUpButton)

    expect(thumbsUpButton).toBeDisabled()

    await waitFor(() => {
      expect(thumbsUpButton).not.toBeDisabled()
    })
  })

  it('should update counts after successful submission', async () => {
    const user = userEvent.setup()

    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    ratingService.submitRating.mockResolvedValue({
      thumbsUpCount: 11,
      thumbsDownCount: 5,
      userRating: 'THUMBS_UP',
    })

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByText('10')).toBeInTheDocument()
    })

    const thumbsUpButton = screen.getByLabelText('Thumbs up')
    await user.click(thumbsUpButton)

    await waitFor(() => {
      expect(screen.getByText('11')).toBeInTheDocument()
    })
  })

  it('should display error message on submission failure', async () => {
    const user = userEvent.setup()

    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    const errorMessage = 'Rate limit exceeded'
    ratingService.submitRating.mockRejectedValue(new Error(errorMessage))

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByLabelText('Thumbs up')).toBeInTheDocument()
    })

    const thumbsUpButton = screen.getByLabelText('Thumbs up')
    await user.click(thumbsUpButton)

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument()
    })
  })

  it('should rollback optimistic update on failure', async () => {
    const user = userEvent.setup()

    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    ratingService.submitRating.mockRejectedValue(new Error('Failed'))

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByText('10')).toBeInTheDocument()
    })

    const thumbsUpButton = screen.getByLabelText('Thumbs up')
    await user.click(thumbsUpButton)

    // After error, counts should revert
    await waitFor(() => {
      expect(screen.getByText('10')).toBeInTheDocument()
      expect(screen.getByText('5')).toBeInTheDocument()
    })
  })

  it('should handle idempotent clicks (same button twice)', async () => {
    const user = userEvent.setup()

    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: 'THUMBS_UP',
    })

    render(
      <SongRating artist="Test Artist" title="Test Song" />
    )

    await waitFor(() => {
      expect(screen.getByLabelText('Thumbs up')).toBeInTheDocument()
    })

    const thumbsUpButton = screen.getByLabelText('Thumbs up')
    await user.click(thumbsUpButton)

    // Should not call submitRating for same button
    expect(ratingService.submitRating).not.toHaveBeenCalled()
  })

  it('should support compact mode', async () => {
    ratingService.getRatingCounts.mockResolvedValue({
      thumbsUpCount: 10,
      thumbsDownCount: 5,
      userRating: null,
    })

    const { container } = render(
      <SongRating artist="Test Artist" title="Test Song" compact={true} />
    )

    await waitFor(() => {
      expect(container.querySelector('.song-rating.compact')).toBeInTheDocument()
    })
  })

  it('should not fetch ratings when artist or title is missing', async () => {
    render(
      <SongRating artist={null} title="Test Song" />
    )

    // Should not call getRatingCounts
    expect(ratingService.getRatingCounts).not.toHaveBeenCalled()
  })
})
