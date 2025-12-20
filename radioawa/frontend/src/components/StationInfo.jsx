import { useStation } from '../contexts/StationContext';
import './StationInfo.css';

function StationInfo() {
  const { currentStation } = useStation();

  if (!currentStation) {
    return null;
  }

  return (
    <div className="station-info-container">
      <div className="station-header">
        {currentStation.logoUrl && (
          <img
            src={currentStation.logoUrl}
            alt={`${currentStation.name} logo`}
            className="station-logo"
          />
        )}
        <div className="station-title-group">
          <h2 className="station-name">{currentStation.name}</h2>
          {currentStation.genre && (
            <div className="station-genre">{currentStation.genre}</div>
          )}
        </div>
      </div>

      {currentStation.tagline && (
        <p className="station-tagline">{currentStation.tagline}</p>
      )}

      {currentStation.description && (
        <p className="station-description">{currentStation.description}</p>
      )}

      <div className="station-technical-details">
        <div className="detail-section">
          <h3 className="section-title">Streaming Details</h3>
          {currentStation.streamFormat && (
            <div className="detail-row">
              <span className="detail-label">Format:</span>
              <span className="detail-value">{currentStation.streamFormat}</span>
            </div>
          )}
          {currentStation.streamQuality && (
            <div className="detail-row">
              <span className="detail-label">Quality:</span>
              <span className="detail-value">{currentStation.streamQuality}</span>
            </div>
          )}
          {currentStation.streamCodec && currentStation.streamBitrate && (
            <div className="detail-row">
              <span className="detail-label">Codec:</span>
              <span className="detail-value">{currentStation.streamCodec} @ {currentStation.streamBitrate}</span>
            </div>
          )}
        </div>

        {currentStation.sourceInfo && (
          <div className="source-info">
            <div className="source-icon">ℹ️</div>
            <div className="source-text">{currentStation.sourceInfo}</div>
          </div>
        )}
      </div>
    </div>
  );
}

export default StationInfo;
