package com.example.eatby.userFav;

import com.google.firebase.firestore.Exclude;

public class UserFavFireStore {
    private String name;
    private String imageUrl;
    private String address;
    private String distance;
    private String category;
    private String price;
    private float rating;
    private String phoneNumber;

    public UserFavFireStore() {}

    public UserFavFireStore(String name, String imageUrl, String address, String distance, String category, String price, float rating,String phoneNumber) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.distance = distance;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoneNumber(){return phoneNumber;}

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber=phoneNumber;}

}
