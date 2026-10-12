package com.roadresale.model;

import java.time.LocalDateTime;

// One advertisement. An Ad "has a" Vehicle (composition),
// and that vehicle can be a Car or a Bike.
public class Ad {
    private int id;                 // 0 means "not saved in the database yet"
    private int sellerId;
    private String sellerName;
    private String sellerPhone;
    private String title;
    private String description;
    private int price;              // in rupees
    private String city;
    private String status = "ACTIVE";   // ACTIVE or SOLD
    private String photoPath;
    private LocalDateTime createdAt;
    private Vehicle vehicle;

    public boolean isSold() {
        return "SOLD".equals(status);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
