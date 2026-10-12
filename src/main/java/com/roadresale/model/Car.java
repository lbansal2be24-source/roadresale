package com.roadresale.model;

// A Car is a Vehicle with a transmission (Manual / Automatic).
public class Car extends Vehicle {
    private String transmission;

    @Override
    public String getType() {
        return "CAR";
    }

    @Override
    public String getExtraDetails() {
        return "Transmission: " + transmission;
    }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
}
