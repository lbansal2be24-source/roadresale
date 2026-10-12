package com.roadresale.repository;

import com.roadresale.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// All SQL for the "users" table lives here.
@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name, email, phone, password_hash) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, user.getName(), user.getEmail(), user.getPhone(), user.getPasswordHash());
    }

    // Returns the user with this email, or null if there is none.
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbc.query(sql, this::mapRow, email);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    // Returns the user with this id, or null if there is none.
    public User findById(int id) {
        List<User> users = jdbc.query("SELECT * FROM users WHERE id = ?", this::mapRow, id);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    // Turns one database row into a User object.
    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        return user;
    }
}
