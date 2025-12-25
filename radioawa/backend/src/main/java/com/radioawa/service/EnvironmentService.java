package com.radioawa.service;

import com.radioawa.dto.EnvironmentInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EnvironmentService {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private final long startTime;

    public EnvironmentService() {
        this.startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    /**
     * Detects if the application is running inside a Docker container
     * Checks multiple indicators for reliability:
     * 1. Presence of /.dockerenv file
     * 2. Docker in cgroup file
     * 3. DEPLOYMENT_MODE environment variable (explicit override)
     */
    public String detectDeploymentMode() {
        // Check explicit environment variable first (highest priority)
        String deploymentMode = System.getenv("DEPLOYMENT_MODE");
        if (deploymentMode != null && !deploymentMode.isEmpty()) {
            return deploymentMode.toLowerCase();
        }

        // Check for /.dockerenv file (Docker creates this)
        if (new File("/.dockerenv").exists()) {
            return "docker";
        }

        // Check cgroup file for docker (more reliable for modern Docker)
        try {
            if (Files.exists(Paths.get("/proc/1/cgroup"))) {
                boolean isDocker = Files.lines(Paths.get("/proc/1/cgroup"))
                        .anyMatch(line -> line.contains("/docker") || line.contains("docker"));
                if (isDocker) {
                    return "docker";
                }
            }
        } catch (IOException e) {
            // Ignore - likely not running on Linux or in Docker
        }

        // Default to local if no Docker indicators found
        return "local";
    }

    /**
     * Get comprehensive environment information
     */
    public EnvironmentInfoResponse getEnvironmentInfo() {
        String deploymentMode = detectDeploymentMode();
        String javaVersion = System.getProperty("java.version");
        long currentTime = System.currentTimeMillis();
        long uptimeSeconds = (currentTime - startTime) / 1000;

        return new EnvironmentInfoResponse(
                deploymentMode,
                activeProfile,
                appVersion,
                javaVersion,
                uptimeSeconds
        );
    }
}