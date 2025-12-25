package com.radioawa.dto;

public class EnvironmentInfoResponse {

    private String deploymentMode;  // "docker" or "local"
    private String activeProfile;   // "dev", "prod", etc.
    private String version;          // Application version
    private String javaVersion;      // JVM version
    private Long uptime;            // Application uptime in seconds

    // Constructors

    public EnvironmentInfoResponse() {
    }

    public EnvironmentInfoResponse(String deploymentMode, String activeProfile,
                                   String version, String javaVersion, Long uptime) {
        this.deploymentMode = deploymentMode;
        this.activeProfile = activeProfile;
        this.version = version;
        this.javaVersion = javaVersion;
        this.uptime = uptime;
    }

    // Getters and Setters

    public String getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(String deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public Long getUptime() {
        return uptime;
    }

    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }
}