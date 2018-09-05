package com.skin.ptech.app.tracking.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Detainees")
public class Detainee {

    @Id
    private String id;
    private String name;
    private String deviceid;
    private String status;
    private String picture;
    private GeoJsonPoint geometry;
    private String timestamp;

    public Detainee() {
    }

    public Detainee(String name, String deviceid, String status, String picture, GeoJsonPoint geometry, String timestamp) {
        this.name = name;
        this.deviceid = deviceid;
        this.status = status;
        this.picture = picture;
        this.geometry = geometry;
        this.timestamp = timestamp;
    }

    public Detainee(String id, String name, String deviceid, String status, String picture, GeoJsonPoint geometry, String timestamp) {
        this.id = id;
        this.name = name;
        this.deviceid = deviceid;
        this.status = status;
        this.picture = picture;
        this.geometry = geometry;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public GeoJsonPoint getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonPoint geometry) {
        this.geometry = geometry;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Detainee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", status='" + status + '\'' +
                ", picture='" + picture + '\'' +
                ", geometry=" + geometry +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
