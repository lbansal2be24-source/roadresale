package com.roadresale.repository;

import com.roadresale.model.Ad;
import com.roadresale.model.Bike;
import com.roadresale.model.Car;
import com.roadresale.model.Vehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// All SQL for the "ads" table lives here.
@Repository
public class AdRepository {
    // Every SELECT also fetches the seller's name and phone from the users table.
    private static final String SELECT_ADS =
            "SELECT a.*, u.name AS seller_name, u.phone AS seller_phone " +
            "FROM ads a JOIN users u ON a.seller_id = u.id ";

    private final JdbcTemplate jdbc;

    public AdRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Ad> findAllActive() {
        return jdbc.query(SELECT_ADS + "WHERE a.status = 'ACTIVE' ORDER BY a.created_at DESC", this::mapRow);
    }

    public List<Ad> findBySeller(int sellerId) {
        return jdbc.query(SELECT_ADS + "WHERE a.seller_id = ? ORDER BY a.created_at DESC", this::mapRow, sellerId);
    }

    // The ads this user saved, most recently saved first.
    public List<Ad> findFavouritesOf(int userId) {
        return jdbc.query(SELECT_ADS + "JOIN favourites f ON f.ad_id = a.id " +
                "WHERE f.user_id = ? ORDER BY f.created_at DESC", this::mapRow, userId);
    }

    // Returns the ad with this id, or null if there is none.
    public Ad findById(int id) {
        List<Ad> ads = jdbc.query(SELECT_ADS + "WHERE a.id = ?", this::mapRow, id);
        if (ads.isEmpty()) {
            return null;
        }
        return ads.get(0);
    }

    public void save(Ad ad) {
        Vehicle v = ad.getVehicle();
        String sql = "INSERT INTO ads (seller_id, title, description, price, city, photo_path, " +
                "vehicle_type, brand, model, model_year, km_driven, fuel_type, transmission, engine_cc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, ad.getSellerId(), ad.getTitle(), ad.getDescription(), ad.getPrice(), ad.getCity(),
                ad.getPhotoPath(), v.getType(), v.getBrand(), v.getModel(), v.getYear(), v.getKmDriven(),
                v.getFuelType(), transmissionOf(v), engineCcOf(v));
    }

    public void update(Ad ad) {
        Vehicle v = ad.getVehicle();
        String sql = "UPDATE ads SET title = ?, description = ?, price = ?, city = ?, photo_path = ?, " +
                "vehicle_type = ?, brand = ?, model = ?, model_year = ?, km_driven = ?, fuel_type = ?, " +
                "transmission = ?, engine_cc = ? WHERE id = ?";
        jdbc.update(sql, ad.getTitle(), ad.getDescription(), ad.getPrice(), ad.getCity(), ad.getPhotoPath(),
                v.getType(), v.getBrand(), v.getModel(), v.getYear(), v.getKmDriven(), v.getFuelType(),
                transmissionOf(v), engineCcOf(v), ad.getId());
    }

    public void updateStatus(int adId, String status) {
        jdbc.update("UPDATE ads SET status = ? WHERE id = ?", status, adId);
    }

    public void delete(int adId) {
        jdbc.update("DELETE FROM ads WHERE id = ?", adId);
    }

    // Only cars have a transmission; for bikes this column stays empty (NULL).
    private String transmissionOf(Vehicle v) {
        if (v instanceof Car) {
            return ((Car) v).getTransmission();
        }
        return null;
    }

    // Only bikes have engine cc; for cars this column stays empty (NULL).
    private Integer engineCcOf(Vehicle v) {
        if (v instanceof Bike) {
            return ((Bike) v).getEngineCc();
        }
        return null;
    }

    // Turns one database row into an Ad object with the right kind of Vehicle inside.
    private Ad mapRow(ResultSet rs, int rowNum) throws SQLException {
        Vehicle vehicle;
        if ("BIKE".equals(rs.getString("vehicle_type"))) {
            Bike bike = new Bike();
            bike.setEngineCc(rs.getInt("engine_cc"));
            vehicle = bike;
        } else {
            Car car = new Car();
            car.setTransmission(rs.getString("transmission"));
            vehicle = car;
        }
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("model_year"));
        vehicle.setKmDriven(rs.getInt("km_driven"));
        vehicle.setFuelType(rs.getString("fuel_type"));

        Ad ad = new Ad();
        ad.setId(rs.getInt("id"));
        ad.setSellerId(rs.getInt("seller_id"));
        ad.setSellerName(rs.getString("seller_name"));
        ad.setSellerPhone(rs.getString("seller_phone"));
        ad.setTitle(rs.getString("title"));
        ad.setDescription(rs.getString("description"));
        ad.setPrice(rs.getInt("price"));
        ad.setCity(rs.getString("city"));
        ad.setStatus(rs.getString("status"));
        ad.setPhotoPath(rs.getString("photo_path"));
        ad.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        ad.setVehicle(vehicle);
        return ad;
    }
}
