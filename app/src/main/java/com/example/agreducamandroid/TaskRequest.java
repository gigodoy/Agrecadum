package com.example.agreducamandroid;

public class TaskRequest {
    private int event_id;
    private String order_number;
    private int order_type;
    private double lat;
    private double lng;
    private boolean status;

    // Constructor
    public TaskRequest(int event_id, String order_number, int order_type, double lat, double lng, boolean status) {
        this.event_id = event_id;
        this.order_number = order_number;
        this.order_type = order_type;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
    }

    // Getters y Setters (opcional si los necesitas)
    public int getEventId() {
        return event_id;
    }

    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

    public String getOrderNumber() {
        return order_number;
    }

    public void setOrderNumber(String order_number) {
        this.order_number = order_number;
    }

    public int getOrderType() {
        return order_type;
    }

    public void setOrderType(int order_type) {
        this.order_type = order_type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

