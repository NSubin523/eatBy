package com.example.eatby.user_list;

public class UsersFireStore {
    private String firstName;
    private String lastName;
    private String userPhone;
    private String userCity;
    private String userEmail;
    private String userPw;
    private String aboutUser;

    public UsersFireStore() {}

    public UsersFireStore(String firstName, String lastName, String userPhone, String userCity, String userEmail, String userPw, String aboutUser) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userPhone = userPhone;
        this.userCity = userCity;
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.aboutUser = aboutUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public void setAboutUser(String aboutUser) {this.aboutUser = aboutUser;}

    public String getAboutUser() {return aboutUser;}
}
