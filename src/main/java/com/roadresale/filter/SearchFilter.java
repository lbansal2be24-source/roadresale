package com.roadresale.filter;

import com.roadresale.model.Ad;

// Every search filter answers one question: "does this ad pass?"
// Adding a new kind of filter = writing one new class. Nothing else changes.
public interface SearchFilter {
    boolean matches(Ad ad);
}
