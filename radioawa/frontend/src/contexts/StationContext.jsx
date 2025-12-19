import { createContext, useContext, useState, useEffect } from 'react';
import { getLastStation, setLastStation } from '../utils/stationStorage';
import { fetchStations } from '../services/stationService';

const StationContext = createContext(null);

export function StationProvider({ children }) {
  const [stations, setStations] = useState([]);
  const [currentStation, setCurrentStation] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch stations from API
    fetchStations().then(data => {
      setStations(data);

      // Restore last selected station or default to first active
      const lastCode = getLastStation();
      const savedStation = data.find(s => s.code === lastCode);
      const defaultStation = savedStation || data.find(s => s.isActive) || data[0];

      setCurrentStation(defaultStation);
      setLoading(false);
    }).catch(error => {
      console.error('Error loading stations:', error);
      setLoading(false);
    });
  }, []);

  const changeStation = (stationCode) => {
    const station = stations.find(s => s.code === stationCode);
    if (station) {
      setCurrentStation(station);
      setLastStation(stationCode);
    }
  };

  return (
    <StationContext.Provider value={{
      stations,
      currentStation,
      changeStation,
      loading
    }}>
      {children}
    </StationContext.Provider>
  );
}

export function useStation() {
  const context = useContext(StationContext);
  if (!context) {
    throw new Error('useStation must be used within StationProvider');
  }
  return context;
}
