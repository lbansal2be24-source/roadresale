package com.roadresale.controller;

import com.roadresale.service.AdService;
import com.roadresale.service.MessageService;
import com.roadresale.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Handles the inbox and the chat page.
// A chat is always about one ad, between me and one other user:  /messages/{adId}/{otherUserId}
@Controller
public class MessageController {
    private final MessageService messageService;
    private final AdService adService;
    private final UserService userService;

    public MessageController(MessageService messageService, AdService adService, UserService userService) {
        this.messageService = messageService;
        this.adService = adService;
        this.userService = userService;
    }

    @GetMapping("/messages")
    public String inbox(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("chats", messageService.getInbox(userId));
        return "inbox";
    }

    @GetMapping("/messages/{adId}/{otherUserId}")
    public String conversation(@PathVariable int adId, @PathVariable int otherUserId,
                               HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        return showConversation(adId, otherUserId, userId, null, model);
    }

    @PostMapping("/messages/{adId}/{otherUserId}")
    public String send(@PathVariable int adId, @PathVariable int otherUserId,
                       @RequestParam String body, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            messageService.send(adId, userId, otherUserId, body);
        } catch (IllegalArgumentException e) {
            return showConversation(adId, otherUserId, userId, e.getMessage(), model);
        }
        // Redirect after saving, so refreshing the page does not send the message twice.
        return "redirect:/messages/" + adId + "/" + otherUserId;
    }

    // Fills the chat page. Used by both methods above.
    private String showConversation(int adId, int otherUserId, int userId, String error, Model model) {
        try {
            model.addAttribute("ad", adService.getAd(adId));
            model.addAttribute("otherUser", userService.getUser(otherUserId));
        } catch (IllegalArgumentException e) {
            return "redirect:/messages";
        }
        model.addAttribute("messages", messageService.getConversation(adId, userId, otherUserId));
        // The chat list on the left side of the page
        model.addAttribute("chats", messageService.getInbox(userId));
        model.addAttribute("error", error);
        return "conversation";
    }
}
