package com.roadresale.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// All SQL for the "favourites" table lives here.
@Repository
public class FavouriteRepository {
    private final JdbcTemplate jdbc;

    public FavouriteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean exists(int userId, int adId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM favourites WHERE user_id = ? AND ad_id = ?", Integer.class, userId, adId);
        return count != null && count > 0;
    }

    public void add(int userId, int adId) {
        jdbc.update("INSERT INTO favourites (user_id, ad_id) VALUES (?, ?)", userId, adId);
    }

    public void remove(int userId, int adId) {
        jdbc.update("DELETE FROM favourites WHERE user_id = ? AND ad_id = ?", userId, adId);
    }
}
