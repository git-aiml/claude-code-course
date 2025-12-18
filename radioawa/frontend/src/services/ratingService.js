const API_BASE = '/api/ratings';

/**
 * Submit a rating for a song
 */
export async function submitRating(artist, title, userId, ratingType) {
  const response = await fetch(API_BASE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      artist,
      title,
      userId,
      ratingType, // 'THUMBS_UP' or 'THUMBS_DOWN'
    }),
  });

  const data = await response.json();

  if (!response.ok) {
    // Extract the actual error message from backend
    const errorMessage = data.message || `Failed to submit rating: ${response.statusText}`;
    throw new Error(errorMessage);
  }

  return data;
}

/**
 * Get rating counts for a song
 */
export async function getRatingCounts(artist, title, userId = null) {
  const params = new URLSearchParams({
    artist,
    title,
  });

  if (userId) {
    params.append('userId', userId);
  }

  const response = await fetch(`${API_BASE}/song?${params.toString()}`);

  if (!response.ok) {
    throw new Error(`Failed to get rating counts: ${response.statusText}`);
  }

  return response.json();
}
