package com.hackaproject.foodswap.foodswap.datamodels;

public class NewEvent {

    private String uid;
    private String food;
    private String date;

    public NewEvent(String uid, String food, String date) {
        this.uid = uid;
        this.food = food;
        this.date = date;
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
}
