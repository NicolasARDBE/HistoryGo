package com.example.historygo.Model;

public class TouristSpot {
    private int id;
    private String address;
    private String description;
    private String name;
    private String openingHours;

    public TouristSpot(int id, String address, String description, String name, String openingHours) {
        this.id = id;
        this.address = address;
        this.description = description;
        this.name = name;
        this.openingHours = openingHours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
}
