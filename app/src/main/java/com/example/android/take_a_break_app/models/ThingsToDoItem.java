package com.example.android.take_a_break_app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by salma gamal on 16/11/2018.
 */

public class ThingsToDoItem {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("opening_hours")
    @Expose
    private String openingHours;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("Website")
    @Expose
    private String Website;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("place_logo")
    @Expose
    private String placeLogo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceLogo() {
        return placeLogo;
    }

    public void setPlaceLogo(String placeLogo) {
        this.placeLogo = placeLogo;
    }
}
