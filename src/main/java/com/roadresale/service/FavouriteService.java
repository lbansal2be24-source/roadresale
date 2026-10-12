package com.roadresale.service;

import com.roadresale.model.Ad;
import com.roadresale.repository.AdRepository;
import com.roadresale.repository.FavouriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Rules for saving ads as favourites.
@Service
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final AdRepository adRepository;

    public FavouriteService(FavouriteRepository favouriteRepository, AdRepository adRepository) {
        this.favouriteRepository = favouriteRepository;
        this.adRepository = adRepository;
    }

    public boolean isFavourite(int userId, int adId) {
        return favouriteRepository.exists(userId, adId);
    }

    // One button does both jobs: saved -> remove it, not saved -> save it.
    public void toggle(int userId, int adId) {
        if (adRepository.findById(adId) == null) {
            throw new IllegalArgumentException("This ad does not exist.");
        }
        if (favouriteRepository.exists(userId, adId)) {
            favouriteRepository.remove(userId, adId);
        } else {
            favouriteRepository.add(userId, adId);
        }
    }

    public List<Ad> getFavourites(int userId) {
        return adRepository.findFavouritesOf(userId);
    }
}
