package com.example.agreducamandroid;

public class TaskRequest {
    private int event_id;
    private String order_number;
    private int order_type;
    private double lat;
    private double lng;
    private int status; // Aquí cambiaste de boolean a int

    public TaskRequest(int event_id, String order_number, int order_type, double lat, double lng, boolean status) {
        this.event_id = event_id;
        this.order_number = order_number;
        this.order_type = order_type;
        this.lat = lat;
        this.lng = lng;
        this.status = status ? 1 : 0; // Convierte booleano a int (1 para true, 0 para false)
    }

    // Getters y Setters
    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
