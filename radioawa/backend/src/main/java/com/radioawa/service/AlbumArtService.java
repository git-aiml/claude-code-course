package com.radioawa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Album Art Service
 * Fetches real album artwork from iTunes Search API
 *
 * Author: Sujit K Singh
 */
@Service
public class AlbumArtService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumArtService.class);
    private static final String ITUNES_API_URL = "https://itunes.apple.com/search";
    private static final String FALLBACK_IMAGE = "https://dummyimage.com/300x300/FF6B35/ffffff.png?text=Music";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Cache to avoid repeated API calls for same song
    private final Map<String, String> artworkCache = new HashMap<>();

    public AlbumArtService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetch album artwork URL for a song
     * @param artist Artist name
     * @param title Song title
     * @return URL to album artwork (high resolution)
     */
    public String fetchAlbumArt(String artist, String title) {
        String cacheKey = artist + "|" + title;

        // Check cache first
        if (artworkCache.containsKey(cacheKey)) {
            logger.debug("Cache hit for: {} - {}", artist, title);
            return artworkCache.get(cacheKey);
        }

        try {
            // Build search query
            String searchTerm = artist + " " + title;
            String url = UriComponentsBuilder.fromHttpUrl(ITUNES_API_URL)
                    .queryParam("term", searchTerm)
                    .queryParam("entity", "song")
                    .queryParam("limit", "1")
                    .build()
                    .toUriString();

            logger.info("Fetching album art from iTunes API: {} - {}", artist, title);
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON response
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("results");

            if (results != null && results.isArray() && results.size() > 0) {
                JsonNode firstResult = results.get(0);
                String artworkUrl = firstResult.get("artworkUrl100").asText();

                // Convert to higher resolution (600x600 instead of 100x100)
                String highResUrl = artworkUrl.replace("100x100bb", "600x600bb");

                logger.info("Found album art: {}", highResUrl);
                artworkCache.put(cacheKey, highResUrl);
                return highResUrl;
            } else {
                logger.warn("No results found for: {} - {}", artist, title);
                return getFallbackImage(title);
            }

        } catch (Exception e) {
            logger.error("Error fetching album art for {} - {}: {}", artist, title, e.getMessage());
            return getFallbackImage(title);
        }
    }

    /**
     * Get fallback image with album/song name
     */
    private String getFallbackImage(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            return "https://dummyimage.com/300x300/FF6B35/ffffff.png?text=" + encodedTitle;
        } catch (Exception e) {
            return FALLBACK_IMAGE;
        }
    }

    /**
     * Clear the artwork cache (for testing/debugging)
     */
    public void clearCache() {
        artworkCache.clear();
        logger.info("Album art cache cleared");
    }

    /**
     * Get cache size (for monitoring)
     */
    public int getCacheSize() {
        return artworkCache.size();
    }
}
