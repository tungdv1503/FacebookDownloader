package com.example.fbvideodownloader.model;

public class Vipmodel {
    String time;
    double price;
    int typeTime;

    public Vipmodel(String time, double price, int typeTime) {
        this.time = time;
        this.price = price;
        this.typeTime = typeTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTypeTime() {
        return typeTime;
    }

    public void setTypeTime(int typeTime) {
        this.typeTime = typeTime;
    }
}
