import { useStation } from '../contexts/StationContext';
import './LiveStream.css';

function LiveStream() {
  const { currentStation } = useStation();

  if (!currentStation) {
    return null;
  }

  return (
    <div className="live-stream">
      <div className="live-stream-header">
        <div className="live-indicator">
          <div className="live-dot"></div>
          <span className="live-label">LIVE</span>
        </div>
        <span className="stream-name">{currentStation.name}</span>
      </div>

      <div className="live-stream-info">
        <div className="broadcast-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
            <path d="M16.24 7.76a6 6 0 0 1 0 8.49M7.76 16.24a6 6 0 0 1 0-8.49M19.07 4.93a10 10 0 0 1 0 14.14M4.93 19.07a10 10 0 0 1 0-14.14"/>
          </svg>
        </div>
        <div className="broadcast-text">
          <div className="broadcast-title">Live Broadcasting</div>
          <div className="broadcast-description">
            {currentStation.genre && <span className="genre-tag">{currentStation.genre}</span>}
            {currentStation.tagline && <div className="tagline-text">{currentStation.tagline}</div>}
          </div>
        </div>
      </div>

      <div className="live-notice">
        <div className="notice-icon">📻</div>
        <div className="notice-text">
          You're listening to a live radio stream. Song information is not available for live broadcasts from this station.
        </div>
      </div>

      <div className="stream-quality-badge">
        <span className="quality-icon">🎵</span>
        <span className="quality-text">
          {currentStation.streamQuality || 'High Quality'} • {currentStation.streamCodec || 'AAC'}
        </span>
      </div>
    </div>
  );
}

export default LiveStream;
