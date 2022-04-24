package com.google.mlkit.vision.demo.realtime_data;

public class DataInfo {
    String location;
    String rTime;
    String counter;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getrTime() {
        return rTime;
    }

    public void setrTime(String rTime) {
        this.rTime = rTime;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public DataInfo(String location, String rTime, String counter) {
        this.location = location;
        this.rTime = rTime;
        this.counter = counter;
    }
}
