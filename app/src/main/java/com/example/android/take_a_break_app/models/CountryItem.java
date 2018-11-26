package com.example.android.take_a_break_app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by salma on 16/11/2018.
 */

public class CountryItem {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("country_logo")
    @Expose
    private String countryLogo;
    @SerializedName("things_to_do")
    @Expose
    private ArrayList<ThingsToDoItem> thingsToDoItems = null;

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

    public String getCountryLogo() {
        return countryLogo;
    }

    public void setCountryLogo(String countryLogo) {
        this.countryLogo = countryLogo;
    }

    public ArrayList<ThingsToDoItem> getThingsToDoItems() {
        return thingsToDoItems;
    }

    public void setThingsToDoItems(ArrayList<ThingsToDoItem> thingsToDoItems) {
        this.thingsToDoItems = thingsToDoItems;
    }
}
