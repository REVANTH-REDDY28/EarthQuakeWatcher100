package com.sunny.earthquakewatcher100.model;

public class EarthQuake {
    private String place;
    private Long time;
    private Double magnitude;
    private String detailUrl;
    private Double latitude;
    private Double longitude;

    public EarthQuake(String place, Long time, Double magnitude, String detailUrl, Double latitude, Double longitude) {
        this.place = place;
        this.time = time;
        this.magnitude = magnitude;
        this.detailUrl = detailUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public EarthQuake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
