package com.hammersmith.onlineapp.model;

/**
 * Created by Thuon on 5/21/2016.
 */
public class Advertise {
    private String image;
    private String logo;

    public Advertise() {
    }

    public Advertise(String image, String logo) {
        this.image = image;
        this.logo = logo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
