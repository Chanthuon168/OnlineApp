package com.hammersmith.onlineapp.model;

/**
 * Created by Thuon on 5/26/2016.
 */
public class User {
    private String name;
    private String email;
    private String facebookID;
    private String socialType;
    private String imageProfile;
    private String phone;
    private String gender;
    private String address;
    private String dob;
    private String id;

    public User() {
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public User(String name, String email, String facebookID, String socialType, String imageProfile, String phone, String gender, String address, String dob, String id) {
        this.name = name;
        this.email = email;
        this.facebookID = facebookID;
        this.socialType = socialType;
        this.imageProfile = imageProfile;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.dob = dob;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getsocialType() {
        return socialType;
    }

    public void setsocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }
}