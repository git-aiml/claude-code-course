package com.radioawa.dto;

public class StationResponse {

    private Long id;
    private String code;
    private String name;
    private String streamUrl;
    private String metadataUrl;
    private Boolean isActive;
    private Integer displayOrder;

    // Constructors

    public StationResponse() {
    }

    public StationResponse(Long id, String code, String name, String streamUrl,
                          String metadataUrl, Boolean isActive, Integer displayOrder) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.streamUrl = streamUrl;
        this.metadataUrl = metadataUrl;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getMetadataUrl() {
        return metadataUrl;
    }

    public void setMetadataUrl(String metadataUrl) {
        this.metadataUrl = metadataUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
