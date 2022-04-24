package com.google.mlkit.vision.demo.history;

public class History {
    String startDestination, endDestination, totalTime, sleepyCount;

    public String getStartDestination() {
        return startDestination;
    }

    public void setStartDestination(String startDestination) {
        this.startDestination = startDestination;
    }

    public String getEndDestination() {
        return endDestination;
    }

    public void setEndDestination(String endDestination) {
        this.endDestination = endDestination;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public History(String startDestination, String endDestination, String totalTime, String sleepyCount) {
        this.startDestination = startDestination;
        this.endDestination = endDestination;
        this.totalTime = totalTime;
        this.sleepyCount = sleepyCount;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getSleepyCount() {
        return sleepyCount;
    }

    public void setSleepyCount(String sleepyCount) {
        this.sleepyCount = sleepyCount;
    }
}
