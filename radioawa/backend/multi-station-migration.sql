-- ============================================================
-- RadioAwa Multi-Station Migration Script
-- Author: Sujit K Singh
-- Description: Adds station support to radioawa
-- ============================================================

-- Step 1: Create stations table
-- ============================================================
CREATE TABLE IF NOT EXISTS stations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    stream_url VARCHAR(500) NOT NULL,
    metadata_url VARCHAR(500) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    stream_format VARCHAR(100),
    stream_quality VARCHAR(100),
    stream_codec VARCHAR(50),
    stream_bitrate VARCHAR(50),
    genre VARCHAR(100),
    tagline VARCHAR(200),
    logo_url VARCHAR(500),
    description VARCHAR(1000),
    source_info VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Step 2: Insert default stations with comprehensive details
-- ============================================================
INSERT INTO stations (code, name, stream_url, metadata_url, is_active, display_order,
                      stream_format, stream_quality, stream_codec, stream_bitrate,
                      genre, tagline, logo_url, description, source_info,
                      created_at, updated_at)
VALUES
    ('ENGLISH', 'RadioAwa English',
     'https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8',
     '/api/metadata/english',
     true, 1,
     'HLS (HTTP Live Streaming)', '24-bit / 48 kHz Lossless', 'AAC', '2304 kbps',
     'Eclectic Mix', 'Crystal-clear lossless audio streaming with live metadata',
     'https://placehold.co/120x120/FF6B35/FFF?text=ENG',
     'RadioAwa English delivers premium quality audio streaming with 24-bit/48kHz lossless sound. Features live metadata updates with real album artwork and a diverse music selection.',
     'Streamed via Amazon CloudFront CDN with metadata enriched by iTunes album artwork',
     NOW(), NOW()),
    ('HINDI', 'Vividh Bharati - All India Radio',
     'https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8',
     '/api/metadata/hindi',
     true, 2,
     'HLS (HTTP Live Streaming)', 'High Quality', 'AAC', '128 kbps',
     'Classic Hindi Film Music', 'Timeless melodies from Bollywood''s golden era',
     'https://placehold.co/120x120/4A90E2/FFF?text=हिंदी',
     'Vividh Bharati is All India Radio''s premier entertainment channel, featuring classic Hindi film songs, cultural programs, and more. Live 24/7 from Mumbai.',
     'Official stream from All India Radio (Prasar Bharati) via BitGravity CDN. Note: Metadata is simulated for demonstration - actual playlist varies.',
     NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

-- Step 3: Create songs table if it doesn't exist (for fresh Docker deployments)
-- ============================================================
CREATE TABLE IF NOT EXISTS songs (
    id BIGSERIAL PRIMARY KEY,
    artist VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    album_art_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Step 4: Create ratings table if it doesn't exist (for fresh Docker deployments)
-- ============================================================
CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY,
    song_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    rating_type VARCHAR(50) NOT NULL,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);

-- Step 5: Add station_id to songs table
-- ============================================================
-- Add column as nullable first for safe migration
ALTER TABLE songs ADD COLUMN IF NOT EXISTS station_id BIGINT;

-- Step 6: Add foreign key constraint (drop first if exists)
-- ============================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_songs_station' AND table_name = 'songs'
    ) THEN
        ALTER TABLE songs ADD CONSTRAINT fk_songs_station
            FOREIGN KEY (station_id) REFERENCES stations(id);
    END IF;
END $$;

-- Step 7: Migrate existing songs to ENGLISH station
-- ============================================================
UPDATE songs
SET station_id = (SELECT id FROM stations WHERE code = 'ENGLISH')
WHERE station_id IS NULL;

-- Step 8: Make station_id non-nullable
-- ============================================================
ALTER TABLE songs ALTER COLUMN station_id SET NOT NULL;

-- Step 9: Update unique constraint (station-scoped)
-- ============================================================
-- Drop old global unique constraint
ALTER TABLE songs DROP CONSTRAINT IF EXISTS songs_artist_title_key;

-- Add new station-scoped unique constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'songs_station_artist_title_key' AND table_name = 'songs'
    ) THEN
        ALTER TABLE songs ADD CONSTRAINT songs_station_artist_title_key
            UNIQUE (station_id, artist, title);
    END IF;
END $$;

-- Step 10: Add index for performance
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_songs_station_id ON songs(station_id);
CREATE INDEX IF NOT EXISTS idx_ratings_song_id ON ratings(song_id);

-- Step 11: Update English station to use album art-enriched metadata endpoint
-- ============================================================
UPDATE stations
SET metadata_url = '/api/metadata/english',
    description = 'RadioAwa English delivers premium quality audio streaming with 24-bit/48kHz lossless sound. Features live metadata updates with real album artwork and a diverse music selection.',
    source_info = 'Streamed via Amazon CloudFront CDN with metadata enriched by iTunes album artwork',
    updated_at = NOW()
WHERE code = 'ENGLISH';

-- ============================================================
-- Verification Queries (Comment out after migration)
-- ============================================================
-- SELECT * FROM stations ORDER BY display_order;
-- SELECT COUNT(*), station_id FROM songs GROUP BY station_id;
-- SELECT s.code, COUNT(sg.id) as song_count
-- FROM stations s
-- LEFT JOIN songs sg ON s.id = sg.station_id
-- GROUP BY s.code, s.name
-- ORDER BY s.display_order;
