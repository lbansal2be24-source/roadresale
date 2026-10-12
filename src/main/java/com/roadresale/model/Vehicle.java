package com.roadresale.model;

// Abstract parent class: holds everything that cars and bikes have in common.
// It is abstract because "a vehicle" on its own is never sold - only a Car or a Bike.
public abstract class Vehicle {
    private String brand;
    private String model;
    private int year;
    private int kmDriven;
    private String fuelType;

    // Each child class says what type it is ("CAR" or "BIKE").
    public abstract String getType();

    // Each child class describes its own special detail.
    public abstract String getExtraDetails();

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getKmDriven() { return kmDriven; }
    public void setKmDriven(int kmDriven) { this.kmDriven = kmDriven; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
}
