package com.roadresale.filter;

import com.roadresale.model.Ad;

// Keeps ads whose price is between min and max.
public class PriceFilter implements SearchFilter {
    private final int min;
    private final int max;

    public PriceFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(Ad ad) {
        return ad.getPrice() >= min && ad.getPrice() <= max;
    }
}
