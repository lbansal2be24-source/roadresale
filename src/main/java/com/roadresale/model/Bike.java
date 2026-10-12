package com.roadresale.model;

// A Bike is a Vehicle with an engine size in cc.
public class Bike extends Vehicle {
    private int engineCc;

    @Override
    public String getType() {
        return "BIKE";
    }

    @Override
    public String getExtraDetails() {
        return "Engine: " + engineCc + " cc";
    }

    public int getEngineCc() { return engineCc; }
    public void setEngineCc(int engineCc) { this.engineCc = engineCc; }
}
