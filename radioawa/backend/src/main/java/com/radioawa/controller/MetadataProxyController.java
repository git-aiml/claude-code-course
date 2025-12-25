package com.radioawa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radioawa.service.AlbumArtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Metadata Proxy Controller
 * Provides metadata for stations that don't have their own metadata endpoints
 * Also enriches external metadata with album artwork from iTunes API
 *
 * Author: Sujit K Singh
 */
@RestController
@RequestMapping("/api/metadata")
public class MetadataProxyController {

    private static final Logger logger = LoggerFactory.getLogger(MetadataProxyController.class);
    private static final String ENGLISH_METADATA_URL = "https://d3d4yli4hf5bmh.cloudfront.net/metadatav2.json";

    private final AlbumArtService albumArtService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Sample Hindi songs representing Vividh Bharati's classic collection
    // Note: Actual songs playing on Vividh Bharati may differ. This shows popular Hindi classics.
    private static final List<Map<String, String>> HINDI_SONGS = Arrays.asList(
        createSong("Arijit Singh", "Tum Hi Ho", "Aashiqui 2"),
        createSong("Shreya Ghoshal", "Sunn Raha Hai", "Aashiqui 2"),
        createSong("Atif Aslam", "Jeene Laga Hoon", "Ramaiya Vastavaiya"),
        createSong("Arijit Singh", "Chahun Main Ya Naa", "Aashiqui 2"),
        createSong("Mohit Chauhan", "Tum Se Hi", "Jab We Met"),
        createSong("Shreya Ghoshal", "Teri Meri", "Bodyguard"),
        createSong("Arijit Singh", "Channa Mereya", "Ae Dil Hai Mushkil"),
        createSong("Neha Kakkar", "Aankh Marey", "Simmba"),
        createSong("Armaan Malik", "Bol Do Na Zara", "Azhar"),
        createSong("Atif Aslam", "Pehli Nazar Mein", "Race"),
        createSong("Arijit Singh", "Ae Dil Hai Mushkil", "Ae Dil Hai Mushkil"),
        createSong("Shreya Ghoshal", "Deewani Mastani", "Bajirao Mastani"),
        createSong("Arijit Singh", "Raabta", "Agent Vinod"),
        createSong("Neha Kakkar", "Dilbar", "Satyameva Jayate"),
        createSong("Sonu Nigam", "Abhi Mujh Mein Kahin", "Agneepath")
    );

    private int currentSongIndex = 0;
    private LocalDateTime lastSongChange = LocalDateTime.now();
    private static final int SONG_DURATION_MINUTES = 4; // Average song duration

    public MetadataProxyController(AlbumArtService albumArtService) {
        this.albumArtService = albumArtService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private static Map<String, String> createSong(String artist, String title, String album) {
        Map<String, String> song = new HashMap<>();
        song.put("artist", artist);
        song.put("title", title);
        song.put("album", album);
        return song;
    }

    /**
     * Get current metadata for Hindi station
     * Simulates a rotating playlist with real album artwork from iTunes API
     */
    @GetMapping("/hindi")
    public ResponseEntity<Map<String, Object>> getHindiMetadata() {
        // Check if it's time to change to next song
        LocalDateTime now = LocalDateTime.now();
        long minutesSinceLastChange = java.time.Duration.between(lastSongChange, now).toMinutes();

        if (minutesSinceLastChange >= SONG_DURATION_MINUTES) {
            currentSongIndex = (currentSongIndex + 1) % HINDI_SONGS.size();
            lastSongChange = now;
        }

        Map<String, String> currentSong = HINDI_SONGS.get(currentSongIndex);
        String artist = currentSong.get("artist");
        String title = currentSong.get("title");
        String album = currentSong.get("album");

        // Fetch real album artwork from iTunes API
        String albumArt = albumArtService.fetchAlbumArt(artist, title);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("artist", artist);
        metadata.put("title", title);
        metadata.put("album", album);
        metadata.put("album_art", albumArt);
        metadata.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Add notice about metadata mismatch
        metadata.put("is_demo", true);
        metadata.put("demo_notice", "⚠️ METADATA MISMATCH: Vividh Bharati is a LIVE radio stream - the actual songs playing are different from what's displayed here. This simulated metadata shows popular Hindi classics for demonstration purposes only.");

        // Add previous songs (recently played)
        List<Map<String, String>> queue = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            int prevIndex = (currentSongIndex - i + HINDI_SONGS.size()) % HINDI_SONGS.size();
            Map<String, String> prevSong = HINDI_SONGS.get(prevIndex);
            Map<String, String> queueSong = new HashMap<>();
            queueSong.put("artist", prevSong.get("artist"));
            queueSong.put("title", prevSong.get("title"));
            queue.add(queueSong);
        }

        // Add to metadata in the format expected by frontend
        for (int i = 0; i < queue.size(); i++) {
            metadata.put("prev_artist_" + (i + 1), queue.get(i).get("artist"));
            metadata.put("prev_title_" + (i + 1), queue.get(i).get("title"));
        }

        return ResponseEntity.ok(metadata);
    }

    /**
     * Get album artwork for Hindi station
     * Returns real artwork from iTunes API
     */
    @GetMapping("/hindi/artwork")
    public ResponseEntity<Map<String, String>> getHindiArtwork() {
        Map<String, String> currentSong = HINDI_SONGS.get(currentSongIndex);
        String artist = currentSong.get("artist");
        String title = currentSong.get("title");

        String albumArt = albumArtService.fetchAlbumArt(artist, title);

        Map<String, String> artwork = new HashMap<>();
        artwork.put("url", albumArt);
        artwork.put("artist", artist);
        artwork.put("title", title);
        return ResponseEntity.ok(artwork);
    }

    /**
     * Manually advance to next song (for testing)
     */
    @PostMapping("/hindi/next")
    public ResponseEntity<Map<String, Object>> nextSong() {
        currentSongIndex = (currentSongIndex + 1) % HINDI_SONGS.size();
        lastSongChange = LocalDateTime.now();
        return getHindiMetadata();
    }

    /**
     * Get playlist info
     */
    @GetMapping("/hindi/playlist")
    public ResponseEntity<Map<String, Object>> getPlaylist() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalSongs", HINDI_SONGS.size());
        response.put("currentIndex", currentSongIndex);
        response.put("currentSong", HINDI_SONGS.get(currentSongIndex));
        response.put("playlist", HINDI_SONGS);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current metadata for English station
     * Proxies CloudFront metadata and enriches with real album artwork from iTunes API
     */
    @GetMapping("/english")
    public ResponseEntity<Map<String, Object>> getEnglishMetadata() {
        try {
            // Fetch metadata from CloudFront
            logger.info("Fetching English station metadata from CloudFront");
            String response = restTemplate.getForObject(ENGLISH_METADATA_URL, String.class);

            // Parse JSON response
            JsonNode jsonNode = objectMapper.readTree(response);

            // Convert to Map
            Map<String, Object> metadata = objectMapper.convertValue(jsonNode, Map.class);

            // Extract artist and title
            String artist = metadata.getOrDefault("artist", "Unknown Artist").toString();
            String title = metadata.getOrDefault("title", "Unknown Track").toString();

            // Fetch real album artwork from iTunes API
            String albumArt = albumArtService.fetchAlbumArt(artist, title);

            // Add album_art to metadata
            metadata.put("album_art", albumArt);

            logger.info("English metadata enriched with album art for: {} - {}", artist, title);
            return ResponseEntity.ok(metadata);

        } catch (Exception e) {
            logger.error("Error fetching English station metadata: {}", e.getMessage());

            // Return fallback metadata
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("artist", "RadioAwa");
            fallback.put("title", "English Station");
            fallback.put("album", "Live Stream");
            fallback.put("album_art", "https://dummyimage.com/300x300/FF6B35/ffffff.png?text=RadioAwa");
            return ResponseEntity.ok(fallback);
        }
    }
}
