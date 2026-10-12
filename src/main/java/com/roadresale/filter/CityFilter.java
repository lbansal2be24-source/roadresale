package com.roadresale.filter;

import com.roadresale.model.Ad;

// Keeps ads from one city (capital/small letters do not matter).
public class CityFilter implements SearchFilter {
    private final String city;

    public CityFilter(String city) {
        this.city = city.trim();
    }

    @Override
    public boolean matches(Ad ad) {
        return ad.getCity().equalsIgnoreCase(city);
    }
}
