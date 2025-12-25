import { useState, useEffect } from 'react'
import { useStation } from '../contexts/StationContext'
import SongRating from './SongRating'
import './NowPlaying.css'

function NowPlaying() {
  const { currentStation } = useStation()
  const [metadata, setMetadata] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [artworkUrl, setArtworkUrl] = useState(null)
  const [artworkError, setArtworkError] = useState(false)

  const metadataUrl = currentStation?.metadataUrl
  const albumArtUrl = currentStation?.albumArtUrl

  const fetchMetadata = async () => {
    if (!metadataUrl) return

    try {
      const response = await fetch(metadataUrl)
      if (!response.ok) {
        throw new Error('Failed to fetch metadata')
      }
      const data = await response.json()

      // Transform prev_* fields into queue array
      const queue = []
      for (let i = 1; i <= 5; i++) {
        const artist = data[`prev_artist_${i}`]
        const title = data[`prev_title_${i}`]
        if (artist && title) {
          queue.push({ artist, title })
        }
      }

      // Add queue to metadata
      const enrichedData = { ...data, queue }
      setMetadata(enrichedData)
      setError(null)

      // Set album art URL from metadata or station config (with cache-busting timestamp)
      // Priority: metadata.album_art > station.albumArtUrl > generic fallback
      const artUrl = data.album_art || albumArtUrl || `https://via.placeholder.com/300x300/FF6B35/FFFFFF?text=${encodeURIComponent(data.album || 'Music')}`
      setArtworkUrl(`${artUrl}?t=${Date.now()}`)
      setArtworkError(false)
    } catch (err) {
      console.error('Error fetching metadata:', err)
      setError('Unable to load track info')
    } finally {
      setLoading(false)
    }
  }

  const handleArtworkError = () => {
    setArtworkError(true)
    console.warn('Failed to load album artwork from server')
  }

  useEffect(() => {
    if (!metadataUrl) return

    // Initial fetch
    fetchMetadata()

    // Auto-refresh every 10 seconds
    const interval = setInterval(fetchMetadata, 10000)

    return () => clearInterval(interval)
  }, [metadataUrl])

  if (loading) {
    return (
      <div className="now-playing">
        <div className="now-playing-header">
          <div className="pulse-dot"></div>
          <span className="now-playing-label">Loading...</span>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="now-playing">
        <div className="now-playing-header">
          <span className="now-playing-label">Now Playing</span>
        </div>
        <div className="now-playing-error">{error}</div>
      </div>
    )
  }

  if (!metadata) {
    return null
  }

  return (
    <div className="now-playing">
      {metadata.is_demo && metadata.demo_notice && (
        <div className="demo-notice-prominent" style={{
          backgroundColor: '#FFF3CD',
          color: '#856404',
          padding: '18px 20px',
          borderRadius: '10px',
          marginBottom: '20px',
          fontSize: '14px',
          textAlign: 'center',
          border: '3px solid #FFC107',
          fontWeight: '600',
          boxShadow: '0 4px 12px rgba(255, 193, 7, 0.3)',
          lineHeight: '1.6',
          animation: 'pulse-border 2s ease-in-out infinite'
        }}>
          <div style={{ fontSize: '32px', marginBottom: '8px', animation: 'bounce 1s ease-in-out infinite' }}>⚠️</div>
          <div style={{ fontWeight: '800', marginBottom: '8px', fontSize: '16px', letterSpacing: '0.5px', textTransform: 'uppercase' }}>
            ⚡ Important: Simulated Metadata ⚡
          </div>
          <div style={{ fontWeight: '600', fontSize: '14px', marginBottom: '6px', color: '#664d03' }}>
            {metadata.demo_notice}
          </div>
          <div style={{ fontSize: '12px', fontWeight: '500', marginTop: '8px', fontStyle: 'italic', color: '#856404' }}>
            ℹ️ The audio stream is real, but track information shown here is for demonstration only
          </div>
        </div>
      )}

      <div className="now-playing-header">
        <div className="pulse-dot"></div>
        <span className="now-playing-label">Now Playing</span>
        <div className="audio-badge">
          <span className="badge-text">24-bit / 48kHz</span>
        </div>
      </div>

      <div className="track-display">
        <div className="album-artwork">
          {artworkUrl && !artworkError ? (
            <img
              src={artworkUrl}
              alt={`${metadata.album || 'Album'} artwork`}
              className="artwork-image"
              onError={handleArtworkError}
            />
          ) : (
            <div className="artwork-placeholder">
              <svg viewBox="0 0 100 100" className="artwork-icon">
                <defs>
                  <linearGradient id="artworkGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style={{stopColor: '#FF6B35', stopOpacity: 1}} />
                    <stop offset="100%" style={{stopColor: '#C1440E', stopOpacity: 1}} />
                  </linearGradient>
                </defs>
                <circle cx="50" cy="50" r="48" fill="url(#artworkGradient)" opacity="0.2"/>
                <path d="M50 25 L50 45 M30 50 L50 50 L50 75 L70 75 L70 50 L50 50"
                      stroke="url(#artworkGradient)" strokeWidth="3" fill="none" strokeLinecap="round"/>
                <circle cx="35" cy="65" r="3" fill="url(#artworkGradient)"/>
                <circle cx="50" cy="65" r="3" fill="url(#artworkGradient)"/>
                <circle cx="65" cy="65" r="3" fill="url(#artworkGradient)"/>
              </svg>
            </div>
          )}
        </div>
        <div className="track-info">
          <div className="track-title">{metadata.title || 'Unknown Track'}</div>
          <div className="track-artist">{metadata.artist || 'Unknown Artist'}</div>
          {metadata.album && (
            <div className="track-album">{metadata.album}</div>
          )}
        </div>
      </div>

      {/* Rating buttons for current track */}
      <div className="current-track-rating">
        <SongRating
          artist={metadata.artist}
          title={metadata.title}
        />
      </div>

      {metadata.queue && metadata.queue.length > 0 && (
        <div className="recently-played">
          <div className="recently-played-header">
            <svg viewBox="0 0 24 24" className="history-icon">
              <path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z" fill="currentColor"/>
            </svg>
            Recently Played
          </div>
          <div className="track-history">
            {metadata.queue.slice(0, 5).map((track, index) => (
              <div key={index} className="history-item">
                <div className="history-number">{index + 1}</div>
                <div className="history-details">
                  <div className="history-track-title">{track.title || 'Unknown Track'}</div>
                  <div className="history-track-artist">{track.artist || 'Unknown Artist'}</div>
                  {/* Rating buttons for history items */}
                  <div className="history-track-rating">
                    <SongRating
                      artist={track.artist}
                      title={track.title}
                      compact={true}
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default NowPlaying
