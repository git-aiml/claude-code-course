package com.radioawa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Metadata Proxy Controller
 * Provides metadata for stations that don't have their own metadata endpoints
 *
 * Author: Sujit K Singh
 */
@RestController
@RequestMapping("/api/metadata")
public class MetadataProxyController {

    // Sample Hindi songs representing Vividh Bharati's classic collection
    // Note: Actual songs playing on Vividh Bharati may differ. This shows popular Hindi classics.
    private static final List<Map<String, String>> HINDI_SONGS = Arrays.asList(
        createSong("Arijit Singh", "Tum Hi Ho", "Aashiqui 2", "https://placehold.co/300x300/FF6B35/FFF?text=Aashiqui+2"),
        createSong("Shreya Ghoshal", "Sunn Raha Hai", "Aashiqui 2", "https://placehold.co/300x300/FF6B35/FFF?text=Aashiqui+2"),
        createSong("Atif Aslam", "Jeene Laga Hoon", "Ramaiya Vastavaiya", "https://placehold.co/300x300/C1440E/FFF?text=Ramaiya"),
        createSong("Arijit Singh", "Chahun Main Ya Naa", "Aashiqui 2", "https://placehold.co/300x300/FF6B35/FFF?text=Aashiqui+2"),
        createSong("Mohit Chauhan", "Tum Se Hi", "Jab We Met", "https://placehold.co/300x300/E74C3C/FFF?text=Jab+We+Met"),
        createSong("Shreya Ghoshal", "Teri Meri", "Bodyguard", "https://placehold.co/300x300/9B59B6/FFF?text=Bodyguard"),
        createSong("Arijit Singh", "Channa Mereya", "Ae Dil Hai Mushkil", "https://placehold.co/300x300/3498DB/FFF?text=Ae+Dil"),
        createSong("Neha Kakkar", "Aankh Marey", "Simmba", "https://placehold.co/300x300/F39C12/FFF?text=Simmba"),
        createSong("Armaan Malik", "Bol Do Na Zara", "Azhar", "https://placehold.co/300x300/1ABC9C/FFF?text=Azhar"),
        createSong("Atif Aslam", "Pehli Nazar Mein", "Race", "https://placehold.co/300x300/E67E22/FFF?text=Race"),
        createSong("Arijit Singh", "Ae Dil Hai Mushkil", "Ae Dil Hai Mushkil", "https://placehold.co/300x300/3498DB/FFF?text=Ae+Dil"),
        createSong("Shreya Ghoshal", "Deewani Mastani", "Bajirao Mastani", "https://placehold.co/300x300/8E44AD/FFF?text=Bajirao"),
        createSong("Arijit Singh", "Raabta", "Agent Vinod", "https://placehold.co/300x300/2ECC71/FFF?text=Agent+Vinod"),
        createSong("Neha Kakkar", "Dilbar", "Satyameva Jayate", "https://placehold.co/300x300/E74C3C/FFF?text=Satyameva"),
        createSong("Sonu Nigam", "Abhi Mujh Mein Kahin", "Agneepath", "https://placehold.co/300x300/C0392B/FFF?text=Agneepath")
    );

    private int currentSongIndex = 0;
    private LocalDateTime lastSongChange = LocalDateTime.now();
    private static final int SONG_DURATION_MINUTES = 4; // Average song duration

    private static Map<String, String> createSong(String artist, String title, String album, String albumArt) {
        Map<String, String> song = new HashMap<>();
        song.put("artist", artist);
        song.put("title", title);
        song.put("album", album);
        song.put("album_art", albumArt);
        return song;
    }

    /**
     * Get current metadata for Hindi station
     * Simulates a rotating playlist
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

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("artist", currentSong.get("artist"));
        metadata.put("title", currentSong.get("title"));
        metadata.put("album", currentSong.get("album"));
        metadata.put("album_art", currentSong.get("album_art"));
        metadata.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Add notice about metadata mismatch
        metadata.put("is_demo", true);
        metadata.put("demo_notice", "This is SIMULATED metadata for demonstration. The actual Vividh Bharati live stream is playing different songs. What you see here is sample data rotating through popular Hindi classics.");

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
     * Returns a placeholder or redirect to actual artwork
     */
    @GetMapping("/hindi/artwork")
    public ResponseEntity<Map<String, String>> getHindiArtwork() {
        Map<String, String> artwork = new HashMap<>();
        artwork.put("url", "https://via.placeholder.com/300x300.png?text=Hindi+Music");
        artwork.put("artist", HINDI_SONGS.get(currentSongIndex).get("artist"));
        artwork.put("title", HINDI_SONGS.get(currentSongIndex).get("title"));
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
}
