package com.example.task101;

public class Truck {

    // variables
    private int image;
    private String name;
    private String description;

    // constructor for full Truck object
    public Truck(int image, String name, String description) {
        this.image = image;
        this.name = name;
        this.description = description;
    }

    // empty constructor. data will be loaded in with setters
    public Truck() {}

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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
}
