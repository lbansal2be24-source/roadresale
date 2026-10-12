package com.roadresale.repository;

import com.roadresale.model.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// All SQL for the "messages" table lives here.
@Repository
public class MessageRepository {
    // Every SELECT also fetches the ad title and both people's names.
    private static final String SELECT_MESSAGES =
            "SELECT m.*, a.title AS ad_title, a.photo_path AS ad_photo, s.name AS sender_name, r.name AS receiver_name " +
            "FROM messages m " +
            "JOIN ads a ON m.ad_id = a.id " +
            "JOIN users s ON m.sender_id = s.id " +
            "JOIN users r ON m.receiver_id = r.id ";

    private final JdbcTemplate jdbc;

    public MessageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(int adId, int senderId, int receiverId, String body) {
        jdbc.update("INSERT INTO messages (ad_id, sender_id, receiver_id, body) VALUES (?, ?, ?, ?)",
                adId, senderId, receiverId, body);
    }

    // Every message this user sent or received, newest first.
    public List<Message> findForUser(int userId) {
        return jdbc.query(SELECT_MESSAGES + "WHERE m.sender_id = ? OR m.receiver_id = ? ORDER BY m.id DESC",
                this::mapRow, userId, userId);
    }

    // The chat between two users about one ad, oldest first.
    public List<Message> findConversation(int adId, int userA, int userB) {
        return jdbc.query(SELECT_MESSAGES +
                        "WHERE m.ad_id = ? AND ((m.sender_id = ? AND m.receiver_id = ?) " +
                        "OR (m.sender_id = ? AND m.receiver_id = ?)) ORDER BY m.id",
                this::mapRow, adId, userA, userB, userB, userA);
    }

    private Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setAdId(rs.getInt("ad_id"));
        m.setAdTitle(rs.getString("ad_title"));
        m.setAdPhoto(rs.getString("ad_photo"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setSenderName(rs.getString("sender_name"));
        m.setReceiverId(rs.getInt("receiver_id"));
        m.setReceiverName(rs.getString("receiver_name"));
        m.setBody(rs.getString("body"));
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return m;
    }
}
