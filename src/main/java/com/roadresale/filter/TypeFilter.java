package com.roadresale.filter;

import com.roadresale.model.Ad;

// Keeps only cars, or only bikes.
public class TypeFilter implements SearchFilter {
    private final String type;   // "CAR" or "BIKE"

    public TypeFilter(String type) {
        this.type = type;
    }

    @Override
    public boolean matches(Ad ad) {
        return ad.getVehicle().getType().equalsIgnoreCase(type);
    }
}
