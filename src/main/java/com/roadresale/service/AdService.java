package com.roadresale.service;

import com.roadresale.filter.CityFilter;
import com.roadresale.filter.KeywordFilter;
import com.roadresale.filter.PriceFilter;
import com.roadresale.filter.SearchFilter;
import com.roadresale.filter.TypeFilter;
import com.roadresale.filter.YearFilter;
import com.roadresale.model.Ad;
import com.roadresale.repository.AdRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

// Rules for posting, editing and searching ads.
@Service
public class AdService {
    private final AdRepository adRepository;
    private final StorageService storageService;

    // We ask for the StorageService interface, not a specific class.
    // That is why switching to S3 later needs no change here.
    public AdService(AdRepository adRepository, StorageService storageService) {
        this.adRepository = adRepository;
        this.storageService = storageService;
    }

    // ---------- Searching ----------

    // Any value can be null/empty, meaning "the buyer did not use this filter".
    public List<Ad> search(String type, String city, Integer minPrice, Integer maxPrice,
                           Integer minYear, String keyword) {
        // Step 1: build the list of filters the buyer actually filled in.
        List<SearchFilter> filters = new ArrayList<>();
        if (type != null && !type.isBlank()) {
            filters.add(new TypeFilter(type));
        }
        if (city != null && !city.isBlank()) {
            filters.add(new CityFilter(city));
        }
        if (minPrice != null || maxPrice != null) {
            int min = (minPrice != null) ? minPrice : 0;
            int max = (maxPrice != null) ? maxPrice : Integer.MAX_VALUE;
            filters.add(new PriceFilter(min, max));
        }
        if (minYear != null) {
            filters.add(new YearFilter(minYear));
        }
        if (keyword != null && !keyword.isBlank()) {
            filters.add(new KeywordFilter(keyword));
        }

        // Step 2: keep only the ads that pass every filter.
        List<Ad> results = new ArrayList<>();
        for (Ad ad : adRepository.findAllActive()) {
            if (passesAll(ad, filters)) {
                results.add(ad);
            }
        }
        return results;
    }

    private boolean passesAll(Ad ad, List<SearchFilter> filters) {
        for (SearchFilter filter : filters) {
            if (!filter.matches(ad)) {
                return false;
            }
        }
        return true;
    }

    // ---------- Reading ----------

    public Ad getAd(int id) {
        Ad ad = adRepository.findById(id);
        if (ad == null) {
            throw new IllegalArgumentException("This ad does not exist.");
        }
        return ad;
    }

    public List<Ad> getAdsBySeller(int sellerId) {
        return adRepository.findBySeller(sellerId);
    }

    // Returns the ad only if it belongs to this user. Stops people editing others' ads.
    public Ad getOwnAd(int adId, int userId) {
        Ad ad = getAd(adId);
        if (ad.getSellerId() != userId) {
            throw new IllegalArgumentException("You can only change your own ads.");
        }
        return ad;
    }

    // ---------- Changing ----------

    public void postAd(Ad ad, MultipartFile photo) {
        validate(ad);
        if (photo != null && !photo.isEmpty()) {
            ad.setPhotoPath(storageService.save(photo));
        }
        adRepository.save(ad);
    }

    public void updateAd(Ad ad, MultipartFile photo, int userId) {
        Ad oldAd = getOwnAd(ad.getId(), userId);
        validate(ad);
        if (photo != null && !photo.isEmpty()) {
            ad.setPhotoPath(storageService.save(photo));
        } else {
            ad.setPhotoPath(oldAd.getPhotoPath());   // no new photo: keep the old one
        }
        adRepository.update(ad);
    }

    public void markSold(int adId, int userId) {
        getOwnAd(adId, userId);
        adRepository.updateStatus(adId, "SOLD");
    }

    public void deleteAd(int adId, int userId) {
        getOwnAd(adId, userId);
        adRepository.delete(adId);
    }

    // Basic checks before anything is saved.
    private void validate(Ad ad) {
        int thisYear = Year.now().getValue();
        if (ad.getTitle().isBlank()) {
            throw new IllegalArgumentException("Please enter a title.");
        }
        if (ad.getCity().isBlank()) {
            throw new IllegalArgumentException("Please enter a city.");
        }
        if (ad.getVehicle().getBrand().isBlank() || ad.getVehicle().getModel().isBlank()) {
            throw new IllegalArgumentException("Please enter the brand and model.");
        }
        if (ad.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be more than 0.");
        }
        if (ad.getVehicle().getYear() < 1980 || ad.getVehicle().getYear() > thisYear) {
            throw new IllegalArgumentException("Year must be between 1980 and " + thisYear + ".");
        }
        if (ad.getVehicle().getKmDriven() < 0) {
            throw new IllegalArgumentException("Km driven cannot be negative.");
        }
    }
}
