import { useStation } from '../contexts/StationContext';
import './StationSelector.css';

function StationSelector() {
  const { stations, currentStation, changeStation } = useStation();

  // Only show active stations
  const activeStations = stations.filter(s => s.isActive);

  if (activeStations.length <= 1) {
    return null; // Don't show selector if only one station
  }

  return (
    <div className="station-selector">
      {activeStations.map(station => (
        <button
          key={station.code}
          className={`station-button ${currentStation?.code === station.code ? 'active' : ''}`}
          onClick={() => changeStation(station.code)}
          aria-label={`Switch to ${station.name}`}
        >
          {station.code === 'HINDI' ? 'हिंदी' : 'English'}
        </button>
      ))}
    </div>
  );
}

export default StationSelector;
