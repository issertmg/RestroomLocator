package com.mobdeve.s15.g16.restroomlocator;

import com.google.firebase.firestore.DocumentId;

public class Restroom {
    @DocumentId
    private String id;
    private String name;
    private double latitude;
    private double longitude;

    public Restroom() {

    }

    public Restroom(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
