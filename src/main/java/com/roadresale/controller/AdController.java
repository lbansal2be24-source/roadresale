package com.roadresale.controller;

import com.roadresale.model.Ad;
import com.roadresale.model.Bike;
import com.roadresale.model.Car;
import com.roadresale.model.Vehicle;
import com.roadresale.service.AdService;
import com.roadresale.service.FavouriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

// Handles every page about ads: search, details, post, edit, sold, delete.
// The controller only reads the request and picks a page. The rules are in AdService.
@Controller
public class AdController {
    private final AdService adService;
    private final FavouriteService favouriteService;

    public AdController(AdService adService, FavouriteService favouriteService) {
        this.adService = adService;
        this.favouriteService = favouriteService;
    }

    // ---------- Pages anyone can open ----------

    // Home page = search page. With no filters it simply shows all active ads.
    @GetMapping("/")
    public String home(@RequestParam(required = false) String type,
                       @RequestParam(required = false) String city,
                       @RequestParam(required = false) Integer minPrice,
                       @RequestParam(required = false) Integer maxPrice,
                       @RequestParam(required = false) Integer minYear,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        model.addAttribute("ads", adService.search(type, city, minPrice, maxPrice, minYear, keyword));
        // Send the filter values back so the form stays filled in.
        model.addAttribute("type", type);
        model.addAttribute("city", city);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minYear", minYear);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @GetMapping("/ads/{id}")
    public String adDetail(@PathVariable int id, HttpSession session, Model model) {
        try {
            model.addAttribute("ad", adService.getAd(id));
        } catch (IllegalArgumentException e) {
            return "redirect:/";
        }
        // Has the logged-in user saved this ad? (decides what the heart button says)
        Integer userId = (Integer) session.getAttribute("userId");
        boolean favourite = (userId != null) && favouriteService.isFavourite(userId, id);
        model.addAttribute("favourite", favourite);
        return "ad-detail";
    }

    // ---------- Pages that need login ----------

    @GetMapping("/my-ads")
    public String myAds(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("ads", adService.getAdsBySeller(userId));
        return "my-ads";
    }

    @GetMapping("/ads/new")
    public String showNewAdForm(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        // An empty ad (id = 0) so the form has something to read from.
        Ad ad = new Ad();
        ad.setVehicle(new Car());
        model.addAttribute("ad", ad);
        return "ad-form";
    }

    @GetMapping("/ads/{id}/edit")
    public String showEditAdForm(@PathVariable int id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            model.addAttribute("ad", adService.getOwnAd(id, userId));
        } catch (IllegalArgumentException e) {
            return "redirect:/my-ads";
        }
        return "ad-form";
    }

    // One method saves both new ads (id = 0) and edited ads (id = the ad's id).
    @PostMapping("/ads/save")
    public String saveAd(@RequestParam int id,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam int price,
                         @RequestParam String city,
                         @RequestParam String vehicleType,
                         @RequestParam String brand,
                         @RequestParam String model,
                         @RequestParam int year,
                         @RequestParam int kmDriven,
                         @RequestParam String fuelType,
                         @RequestParam(required = false) String transmission,
                         @RequestParam(required = false) Integer engineCc,
                         @RequestParam(required = false) MultipartFile photo,
                         HttpSession session, Model pageModel) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // Make the right kind of vehicle: this is where Car or Bike is chosen.
        Vehicle vehicle;
        if (vehicleType.equals("BIKE")) {
            Bike bike = new Bike();
            bike.setEngineCc(engineCc != null ? engineCc : 0);
            vehicle = bike;
        } else {
            Car car = new Car();
            car.setTransmission(transmission != null ? transmission : "Manual");
            vehicle = car;
        }
        vehicle.setBrand(brand.trim());
        vehicle.setModel(model.trim());
        vehicle.setYear(year);
        vehicle.setKmDriven(kmDriven);
        vehicle.setFuelType(fuelType);

        Ad ad = new Ad();
        ad.setId(id);
        ad.setSellerId(userId);
        ad.setTitle(title.trim());
        ad.setDescription(description.trim());
        ad.setPrice(price);
        ad.setCity(city.trim());
        ad.setVehicle(vehicle);

        try {
            if (id == 0) {
                adService.postAd(ad, photo);
            } else {
                adService.updateAd(ad, photo, userId);
            }
        } catch (IllegalArgumentException e) {
            // Show the form again with the error and what the seller typed.
            pageModel.addAttribute("error", e.getMessage());
            pageModel.addAttribute("ad", ad);
            return "ad-form";
        }
        return "redirect:/my-ads";
    }

    @PostMapping("/ads/{id}/sold")
    public String markSold(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            adService.markSold(id, userId);
        } catch (IllegalArgumentException e) {
            // Not this user's ad: do nothing.
        }
        return "redirect:/my-ads";
    }

    @PostMapping("/ads/{id}/delete")
    public String deleteAd(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            adService.deleteAd(id, userId);
        } catch (IllegalArgumentException e) {
            // Not this user's ad: do nothing.
        }
        return "redirect:/my-ads";
    }
}
