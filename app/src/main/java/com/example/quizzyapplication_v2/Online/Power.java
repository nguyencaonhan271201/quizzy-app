package com.example.quizzyapplication_v2.Online;

import android.graphics.Bitmap;

public class Power {
    private int id;
    private String name;
    private String description;
    private Bitmap icon;
    private int price;
    private int quantity;

    public Power(int id, String name, String description, Bitmap icon, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
