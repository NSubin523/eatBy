package com.example.eatby.review_class;

public class ReviewDb{
    private String restaurantName;
    private String image;
    private String review;
    private float userRating;

    public ReviewDb() {}

    public ReviewDb(String restaurantName, String image, String review, float userRating) {
        this.restaurantName = restaurantName;
        this.image = image;
        this.review = review;
        this.userRating = userRating;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }
}