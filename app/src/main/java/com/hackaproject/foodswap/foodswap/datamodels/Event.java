package com.hackaproject.foodswap.foodswap.datamodels;

public class Event {

    private String uid;
    private String food;
    private String date;
    private String status;
    private String partnerId;

    public Event(String uid, String food, String date, String status, String partnerId) {
        this.uid = uid;
        this.food = food;
        this.date = date;
        this.status = status;
        this.partnerId = partnerId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatusInteger() {
        return Integer.parseInt(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}