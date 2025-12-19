package com.radioawa.controller;

import com.radioawa.dto.RatingCountsResponse;
import com.radioawa.dto.RatingRequest;
import com.radioawa.dto.RatingResponse;
import com.radioawa.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(
            @Valid @RequestBody RatingRequest request,
            HttpServletRequest httpRequest) {
        try {
            // Capture IP address from request
            String ipAddress = getClientIpAddress(httpRequest);
            request.setIpAddress(ipAddress);

            RatingResponse response = ratingService.submitRating(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Handle rate limiting and other business logic errors
            RatingResponse errorResponse = new RatingResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        } catch (Exception e) {
            RatingResponse errorResponse = new RatingResponse();
            errorResponse.setMessage("Failed to submit rating: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Extract client IP address, handling proxies and load balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }

    @GetMapping("/counts")
    public ResponseEntity<RatingCountsResponse> getRatingCounts(
            @RequestParam String stationCode,
            @RequestParam String artist,
            @RequestParam String title,
            @RequestParam(required = false) String userId) {
        try {
            RatingCountsResponse response = ratingService.getRatingCounts(stationCode, artist, title, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
