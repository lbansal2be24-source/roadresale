package com.roadresale.service;

import com.roadresale.model.Ad;
import com.roadresale.model.Message;
import com.roadresale.repository.AdRepository;
import com.roadresale.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Rules for messages between buyers and sellers.
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AdRepository adRepository;

    public MessageService(MessageRepository messageRepository, AdRepository adRepository) {
        this.messageRepository = messageRepository;
        this.adRepository = adRepository;
    }

    // The inbox: one row per chat, showing only the newest message of each chat.
    public List<Message> getInbox(int userId) {
        // A chat is identified by "ad id + the other person's id".
        // The messages arrive newest first, so the first one we see for a chat is its latest.
        Map<String, Message> latestPerChat = new LinkedHashMap<>();
        for (Message m : messageRepository.findForUser(userId)) {
            String chatKey = m.getAdId() + "-" + m.getOtherUserId(userId);
            if (!latestPerChat.containsKey(chatKey)) {
                latestPerChat.put(chatKey, m);
            }
        }
        return new ArrayList<>(latestPerChat.values());
    }

    public List<Message> getConversation(int adId, int myId, int otherUserId) {
        return messageRepository.findConversation(adId, myId, otherUserId);
    }

    public void send(int adId, int senderId, int receiverId, String body) {
        Ad ad = adRepository.findById(adId);
        if (ad == null) {
            throw new IllegalArgumentException("This ad does not exist.");
        }
        if (senderId == receiverId) {
            throw new IllegalArgumentException("You cannot message yourself.");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Please type a message.");
        }
        if (body.length() > 1000) {
            throw new IllegalArgumentException("Message is too long (1000 characters at most).");
        }

        // Who may talk about this ad?
        // - a buyer may only write to the seller
        // - the seller may only reply to a buyer who has already written
        if (senderId == ad.getSellerId()) {
            if (messageRepository.findConversation(adId, senderId, receiverId).isEmpty()) {
                throw new IllegalArgumentException("You can only reply to buyers who messaged you.");
            }
        } else if (receiverId != ad.getSellerId()) {
            throw new IllegalArgumentException("You can only message the seller of this ad.");
        }

        messageRepository.save(adId, senderId, receiverId, body.trim());
    }
}
