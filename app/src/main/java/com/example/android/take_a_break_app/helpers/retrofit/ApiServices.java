package com.example.android.take_a_break_app.helpers.retrofit;

import com.example.android.take_a_break_app.models.CountryItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by salma gamal on 19/10/2018.
 */

public interface ApiServices {

    @GET("api/json/get/VkbHYga6B/")
    Call<ArrayList<CountryItem>> getCountries();

}
