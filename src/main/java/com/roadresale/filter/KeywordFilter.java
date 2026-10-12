package com.roadresale.filter;

import com.roadresale.model.Ad;

// Keeps ads where the word appears in the title, brand or model.
public class KeywordFilter implements SearchFilter {
    private final String keyword;

    public KeywordFilter(String keyword) {
        this.keyword = keyword.trim().toLowerCase();
    }

    @Override
    public boolean matches(Ad ad) {
        String text = ad.getTitle() + " " + ad.getVehicle().getBrand() + " " + ad.getVehicle().getModel();
        return text.toLowerCase().contains(keyword);
    }
}
