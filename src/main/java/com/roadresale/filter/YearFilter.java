package com.roadresale.filter;

import com.roadresale.model.Ad;

// Keeps vehicles made in this year or later.
public class YearFilter implements SearchFilter {
    private final int minYear;

    public YearFilter(int minYear) {
        this.minYear = minYear;
    }

    @Override
    public boolean matches(Ad ad) {
        return ad.getVehicle().getYear() >= minYear;
    }
}
