package com.roadresale.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// One chat message between a buyer and a seller about one ad.
public class Message {
    private int id;
    private int adId;
    private String adTitle;
    private String adPhoto;
    private int senderId;
    private String senderName;
    private int receiverId;
    private String receiverName;
    private String body;
    private LocalDateTime createdAt;

    // "The other person" in the chat, seen from my side.
    public int getOtherUserId(int myId) {
        return (senderId == myId) ? receiverId : senderId;
    }

    public String getOtherUserName(int myId) {
        return (senderId == myId) ? receiverName : senderName;
    }

    // Text for the date line in the chat: "Today", "Yesterday" or "12 Oct 2026".
    public String getDayLabel() {
        LocalDate day = createdAt.toLocalDate();
        LocalDate today = LocalDate.now();
        if (day.equals(today)) {
            return "Today";
        }
        if (day.equals(today.minusDays(1))) {
            return "Yesterday";
        }
        return day.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAdId() { return adId; }
    public void setAdId(int adId) { this.adId = adId; }

    public String getAdTitle() { return adTitle; }
    public void setAdTitle(String adTitle) { this.adTitle = adTitle; }

    public String getAdPhoto() { return adPhoto; }
    public void setAdPhoto(String adPhoto) { this.adPhoto = adPhoto; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
