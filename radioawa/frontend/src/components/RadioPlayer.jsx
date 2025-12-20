import { useState, useEffect, useRef } from 'react'
import Hls from 'hls.js'
import { useStation } from '../contexts/StationContext'
import NowPlaying from './NowPlaying'
import StationInfo from './StationInfo'
import './RadioPlayer.css'

function RadioPlayer() {
  const { currentStation } = useStation()
  const audioRef = useRef(null)
  const hlsRef = useRef(null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [volume, setVolume] = useState(70)
  const [status, setStatus] = useState('offline')
  const [error, setError] = useState('')

  const streamUrl = currentStation?.streamUrl

  useEffect(() => {
    const audio = audioRef.current
    if (!audio || !streamUrl) return

    // Cleanup previous HLS instance if exists
    if (hlsRef.current) {
      hlsRef.current.destroy()
      hlsRef.current = null
    }

    if (Hls.isSupported()) {
      const hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: 90
      })

      hls.loadSource(streamUrl)
      hls.attachMedia(audio)

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        console.log('HLS manifest loaded')
        setStatus('ready')
      })

      hls.on(Hls.Events.ERROR, (event, data) => {
        console.error('HLS error:', data)
        if (data.fatal) {
          switch(data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              showError('Network error. Please check your connection and try again.')
              hls.startLoad()
              break
            case Hls.ErrorTypes.MEDIA_ERROR:
              showError('Media error. Attempting to recover...')
              hls.recoverMediaError()
              break
            default:
              showError('Fatal error occurred. Please refresh the page.')
              setStatus('error')
              break
          }
        }
      })

      hlsRef.current = hls

      return () => {
        hls.destroy()
      }
    } else if (audio.canPlayType('application/vnd.apple.mpegurl')) {
      audio.src = streamUrl
      setStatus('ready')
    } else {
      showError('HLS is not supported in your browser.')
      setStatus('error')
    }
  }, [streamUrl])

  useEffect(() => {
    const audio = audioRef.current
    if (!audio) return

    audio.volume = volume / 100

    const handleWaiting = () => setStatus('loading')
    const handlePlaying = () => setStatus('live')
    const handlePause = () => {
      if (isPlaying) setStatus('loading')
    }

    audio.addEventListener('waiting', handleWaiting)
    audio.addEventListener('playing', handlePlaying)
    audio.addEventListener('pause', handlePause)

    return () => {
      audio.removeEventListener('waiting', handleWaiting)
      audio.removeEventListener('playing', handlePlaying)
      audio.removeEventListener('pause', handlePause)
    }
  }, [isPlaying, volume])

  const showError = (message) => {
    setError(message)
    setTimeout(() => setError(''), 5000)
  }

  const togglePlay = async () => {
    const audio = audioRef.current
    if (!audio) return

    if (!isPlaying) {
      try {
        await audio.play()
        setIsPlaying(true)
        setStatus('live')
      } catch (err) {
        console.error('Playback error:', err)
        showError('Failed to start playback. Please try again.')
      }
    } else {
      audio.pause()
      setIsPlaying(false)
      setStatus('ready')
    }
  }

  const handleVolumeChange = (e) => {
    const newVolume = parseInt(e.target.value)
    setVolume(newVolume)
  }

  const getStatusClass = () => {
    switch(status) {
      case 'ready': return 'status-ready'
      case 'loading': return 'status-loading'
      case 'live': return 'status-live'
      case 'error': return 'status-error'
      default: return 'status-offline'
    }
  }

  const getStatusText = () => {
    switch(status) {
      case 'ready': return 'Ready to Play'
      case 'loading': return 'Loading...'
      case 'live': return 'LIVE'
      case 'error': return 'Error'
      default: return 'Offline'
    }
  }

  return (
    <div className="radio-player">
      <div className="logo-container">
        <svg className="logo-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="logoGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style={{stopColor: '#FF6B35', stopOpacity: 1}} />
              <stop offset="100%" style={{stopColor: '#C1440E', stopOpacity: 1}} />
            </linearGradient>
            <circle id="peachCircle" cx="100" cy="100" r="90" fill="#FFE8D6"/>
          </defs>

          <use href="#peachCircle"/>
          <circle cx="100" cy="100" r="90" fill="none" stroke="#C1440E" strokeWidth="3"/>

          <g className="waves">
            <path className="wave" d="M 60 100 Q 60 70, 80 60" fill="none" stroke="url(#logoGradient)" strokeWidth="4" strokeLinecap="round" opacity="0.6"/>
            <path className="wave" d="M 50 100 Q 50 55, 80 40" fill="none" stroke="url(#logoGradient)" strokeWidth="4" strokeLinecap="round" opacity="0.6"/>
            <path className="wave" d="M 40 100 Q 40 40, 80 20" fill="none" stroke="url(#logoGradient)" strokeWidth="4" strokeLinecap="round" opacity="0.6"/>

            <path className="wave" d="M 140 100 Q 140 70, 120 60" fill="none" stroke="url(#logoGradient)" strokeWidth="4" strokeLinecap="round" opacity="0.6"/>
            <path className="wave" d="M 150 100 Q 150 55, 120 40" fill="none" stroke="url(#logoGradient)" strokeWidth="4" strokeLinecap="round" opacity="0.6"/>
            <path className="wave" d="M 160 100 Q 160 40, 120 20" fill="none" stroke="url(#logoGradient)" strokeWidth="4" strokeLinecap="round" opacity="0.6"/>
          </g>

          <circle cx="100" cy="100" r="12" fill="url(#logoGradient)"/>
          <rect x="97" y="112" width="6" height="30" fill="url(#logoGradient)" rx="3"/>
          <path d="M 85 142 L 100 135 L 115 142 Z" fill="url(#logoGradient)"/>

          <text x="100" y="175" fontFamily="Montserrat, Arial, sans-serif" fontSize="32" fontWeight="bold" fill="#C1440E" textAnchor="middle">AWA</text>
        </svg>
      </div>

      <h1 className="radio-title">radioawa</h1>
      <p className="radio-subtitle">24-BIT LOSSLESS STREAM</p>
      <p className="tagline">Crystal-clear audio, ad-free, always on</p>

      <div className={`status-indicator ${getStatusClass()}`}>
        {getStatusText()}
      </div>

      <button
        className="play-button"
        onClick={togglePlay}
        aria-label={isPlaying ? 'Pause' : 'Play'}
      >
        {isPlaying ? (
          <svg viewBox="0 0 24 24" className="control-icon">
            <path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z" fill="#FFFFFF"/>
          </svg>
        ) : (
          <svg viewBox="0 0 24 24" className="control-icon">
            <path d="M8 5v14l11-7z" fill="#FFFFFF"/>
          </svg>
        )}
      </button>

      <div className="volume-control">
        <svg className="volume-icon" viewBox="0 0 24 24">
          <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z" fill="#231F20"/>
        </svg>
        <input
          type="range"
          min="0"
          max="100"
          value={volume}
          onChange={handleVolumeChange}
          className="volume-slider"
          aria-label="Volume"
        />
        <span className="volume-value">{volume}%</span>
      </div>

      {isPlaying && <NowPlaying />}

      <StationInfo />

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <audio ref={audioRef} preload="none" />
    </div>
  )
}

export default RadioPlayer
