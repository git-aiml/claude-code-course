const API_BASE = 'http://localhost:8081/api/stations';

export async function fetchStations() {
  try {
    const response = await fetch(API_BASE);
    if (!response.ok) {
      throw new Error('Failed to fetch stations');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching stations:', error);
    // Return default English station as fallback
    return [{
      id: 1,
      code: 'ENGLISH',
      name: 'RadioAwa English',
      streamUrl: 'https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8',
      metadataUrl: 'https://d3d4yli4hf5bmh.cloudfront.net/metadatav2.json',
      isActive: true,
      displayOrder: 1
    }];
  }
}

export async function getStationByCode(code) {
  const response = await fetch(`${API_BASE}/${code}`);
  if (!response.ok) {
    throw new Error(`Station not found: ${code}`);
  }
  return await response.json();
}
