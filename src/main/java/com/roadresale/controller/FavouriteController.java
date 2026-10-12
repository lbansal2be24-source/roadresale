package com.roadresale.controller;

import com.roadresale.service.FavouriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// Handles saving ads and the "Favourites" page.
@Controller
public class FavouriteController {
    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @GetMapping("/favourites")
    public String favourites(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("ads", favouriteService.getFavourites(userId));
        return "favourites";
    }

    // The heart button on the ad page: saves the ad, or removes it if already saved.
    @PostMapping("/ads/{id}/favourite")
    public String toggleFavourite(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            favouriteService.toggle(userId, id);
        } catch (IllegalArgumentException e) {
            return "redirect:/";
        }
        return "redirect:/ads/" + id;
    }
}
