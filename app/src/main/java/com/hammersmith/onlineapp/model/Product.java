package com.hammersmith.onlineapp.model;

import java.text.DecimalFormat;

/**
 * Created by Thuon on 5/20/2016.
 */
public class Product {
    private String image;
    private String title;
    private String price;
    private String discount;

    public Product() {
    }

    public Product(String image, String title, String price, String discount) {
        this.image = image;
        this.title = title;
        this.price = price;
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return this.discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
