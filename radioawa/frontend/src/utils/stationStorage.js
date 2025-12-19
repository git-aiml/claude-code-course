const STATION_KEY = 'radioawa_selected_station';

export function getLastStation() {
  return localStorage.getItem(STATION_KEY) || 'ENGLISH';
}

export function setLastStation(stationCode) {
  localStorage.setItem(STATION_KEY, stationCode);
}

export function clearLastStation() {
  localStorage.removeItem(STATION_KEY);
}
