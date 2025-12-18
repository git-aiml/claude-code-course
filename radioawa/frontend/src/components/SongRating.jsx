import { useState, useEffect } from 'react';
import { getUserId } from '../utils/userIdentity';
import { submitRating, getRatingCounts } from '../services/ratingService';
import './SongRating.css';

function SongRating({ artist, title, compact = false }) {
  const [userId] = useState(getUserId());
  const [ratings, setRatings] = useState({
    thumbsUpCount: 0,
    thumbsDownCount: 0,
    userRating: null,
  });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!artist || !title) return;

    const fetchRatings = async () => {
      try {
        const data = await getRatingCounts(artist, title, userId);
        setRatings({
          thumbsUpCount: data.thumbsUpCount || 0,
          thumbsDownCount: data.thumbsDownCount || 0,
          userRating: data.userRating,
        });
        setError(null);
      } catch (err) {
        console.error('Error fetching ratings:', err);
        setError('Failed to load ratings');
      } finally {
        setLoading(false);
      }
    };

    fetchRatings();
  }, [artist, title, userId]);

  const handleRating = async (ratingType) => {
    if (submitting) return;

    setSubmitting(true);
    setError(null);

    // Optimistic update
    const previousRatings = { ...ratings };
    const newRatings = { ...ratings };

    if (ratings.userRating === ratingType) {
      // User clicked same button - no change (idempotent)
      setSubmitting(false);
      return;
    }

    if (ratings.userRating === null) {
      // New rating
      if (ratingType === 'THUMBS_UP') {
        newRatings.thumbsUpCount += 1;
      } else {
        newRatings.thumbsDownCount += 1;
      }
    } else {
      // Changing rating
      if (ratings.userRating === 'THUMBS_UP') {
        newRatings.thumbsUpCount = Math.max(0, newRatings.thumbsUpCount - 1);
        newRatings.thumbsDownCount += 1;
      } else {
        newRatings.thumbsDownCount = Math.max(0, newRatings.thumbsDownCount - 1);
        newRatings.thumbsUpCount += 1;
      }
    }

    newRatings.userRating = ratingType;
    setRatings(newRatings);

    try {
      const response = await submitRating(artist, title, userId, ratingType);
      // Update with server response
      setRatings({
        thumbsUpCount: response.thumbsUpCount,
        thumbsDownCount: response.thumbsDownCount,
        userRating: response.userRating,
      });
      setError(null); // Clear any previous errors
    } catch (err) {
      console.error('Error submitting rating:', err);
      // Display the actual error message from backend
      setError(err.message || 'Failed to submit rating');
      // Rollback optimistic update
      setRatings(previousRatings);
      // Auto-clear error after 5 seconds
      setTimeout(() => setError(null), 5000);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className={`song-rating ${compact ? 'compact' : ''}`}>
        <div className="rating-loading">Loading ratings...</div>
      </div>
    );
  }

  if (error && ratings.thumbsUpCount === 0 && ratings.thumbsDownCount === 0) {
    return (
      <div className={`song-rating ${compact ? 'compact' : ''}`}>
        <div className="rating-error">{error}</div>
      </div>
    );
  }

  return (
    <div className={`song-rating ${compact ? 'compact' : ''}`}>
      {error && (
        <div className="rating-error-message">{error}</div>
      )}
      <div className="rating-buttons-container">
        <button
          className={`rating-button thumbs-up ${ratings.userRating === 'THUMBS_UP' ? 'active' : ''}`}
          onClick={() => handleRating('THUMBS_UP')}
          disabled={submitting}
          aria-label="Thumbs up"
        >
          <svg viewBox="0 0 24 24" className="rating-icon">
            <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z" fill="currentColor"/>
          </svg>
          <span className="rating-count">{ratings.thumbsUpCount}</span>
        </button>

        <button
          className={`rating-button thumbs-down ${ratings.userRating === 'THUMBS_DOWN' ? 'active' : ''}`}
          onClick={() => handleRating('THUMBS_DOWN')}
          disabled={submitting}
          aria-label="Thumbs down"
        >
          <svg viewBox="0 0 24 24" className="rating-icon">
            <path d="M15 3H6c-.83 0-1.54.5-1.84 1.22l-3.02 7.05c-.09.23-.14.47-.14.73v2c0 1.1.9 2 2 2h6.31l-.95 4.57-.03.32c0 .41.17.79.44 1.06L9.83 23l6.59-6.59c.36-.36.58-.86.58-1.41V5c0-1.1-.9-2-2-2zm4 0v12h4V3h-4z" fill="currentColor"/>
          </svg>
          <span className="rating-count">{ratings.thumbsDownCount}</span>
        </button>
      </div>
    </div>
  );
}

export default SongRating;
