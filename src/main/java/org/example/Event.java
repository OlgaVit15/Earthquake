package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class Event {
    String eventTime;
    String region;
    double depth;
    double lon;
    double lat;
    double mag;
    String corr_region = "";

    // Конструктор по умолчанию
    public Event() {
    }

    // Конструктор с параметрами (опционально, если вам нужно)
    @JsonCreator
    public Event(
            @JsonProperty("eventTime") String eventTime,
            @JsonProperty("region") String region,
            @JsonProperty("depth") double depth,
            @JsonProperty("lon") double lon,
            @JsonProperty("lat") double lat,
            @JsonProperty("mag") double mag) {
        this.eventTime = eventTime;
        this.region = region;
        this.depth = depth;
        this.lon = lon;
        this.lat = lat;
        this.mag = mag;
    }


    // Геттеры и сеттеры
    public String getEventTime() {
        return eventTime;
    }

    @JsonIgnore
    public long getEventTimelng() {
        return OffsetDateTime.parse(eventTime).toEpochSecond();
    }


    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getMag() {
        return mag;
    }

    public void setMag(double mag) {
        this.mag = mag;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;}
}
