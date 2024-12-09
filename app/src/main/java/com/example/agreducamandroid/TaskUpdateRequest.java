package com.example.agreducamandroid;

public class TaskUpdateRequest {

    private int eventId;
    private int scannedCode;
    private double latitude;
    private double longitude;

    // Constructor
    public TaskUpdateRequest(int eventId, int scannedCode, double latitude, double longitude) {
        this.eventId = eventId;
        this.scannedCode = scannedCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getScannedCode() {
        return scannedCode;
    }

    public void setScannedCode(int scannedCode) {
        this.scannedCode = scannedCode;
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
