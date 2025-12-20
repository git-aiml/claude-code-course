package com.radioawa.dto;

public class StationResponse {

    private Long id;
    private String code;
    private String name;
    private String streamUrl;
    private String metadataUrl;
    private Boolean isActive;
    private Integer displayOrder;
    private String streamFormat;
    private String streamQuality;
    private String streamCodec;
    private String streamBitrate;
    private String genre;
    private String tagline;
    private String logoUrl;
    private String description;
    private String sourceInfo;

    // Constructors

    public StationResponse() {
    }

    public StationResponse(Long id, String code, String name, String streamUrl,
                          String metadataUrl, Boolean isActive, Integer displayOrder,
                          String streamFormat, String streamQuality, String streamCodec, String streamBitrate,
                          String genre, String tagline, String logoUrl, String description, String sourceInfo) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.streamUrl = streamUrl;
        this.metadataUrl = metadataUrl;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
        this.streamFormat = streamFormat;
        this.streamQuality = streamQuality;
        this.streamCodec = streamCodec;
        this.streamBitrate = streamBitrate;
        this.genre = genre;
        this.tagline = tagline;
        this.logoUrl = logoUrl;
        this.description = description;
        this.sourceInfo = sourceInfo;
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

    public String getStreamFormat() {
        return streamFormat;
    }

    public void setStreamFormat(String streamFormat) {
        this.streamFormat = streamFormat;
    }

    public String getStreamQuality() {
        return streamQuality;
    }

    public void setStreamQuality(String streamQuality) {
        this.streamQuality = streamQuality;
    }

    public String getStreamCodec() {
        return streamCodec;
    }

    public void setStreamCodec(String streamCodec) {
        this.streamCodec = streamCodec;
    }

    public String getStreamBitrate() {
        return streamBitrate;
    }

    public void setStreamBitrate(String streamBitrate) {
        this.streamBitrate = streamBitrate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
}
