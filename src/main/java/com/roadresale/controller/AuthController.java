package com.roadresale.controller;

import com.roadresale.model.User;
import com.roadresale.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Handles the register, login and logout pages.
@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String email,
                           @RequestParam String phone, @RequestParam String password, Model model) {
        try {
            userService.register(name, email, phone, password);
        } catch (IllegalArgumentException e) {
            // Show the form again with the error and what the user typed.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            return "register";
        }
        return "redirect:/login?registered=yes";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, Model model) {
        User user = userService.login(email, password);
        if (user == null) {
            model.addAttribute("error", "Wrong email or password.");
            model.addAttribute("email", email);
            return "login";
        }
        // Remember who is logged in. The session lives on the server.
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
