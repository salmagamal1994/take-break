package com.example.android.take_a_break_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.take_a_break_app.adapters.CountriesAdapter;
import com.example.android.take_a_break_app.helpers.NetworkUtils;
import com.example.android.take_a_break_app.helpers.Utils;
import com.example.android.take_a_break_app.helpers.retrofit.ApiServices;
import com.example.android.take_a_break_app.helpers.retrofit.RetrofitClient;
import com.example.android.take_a_break_app.models.CountryItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.adView)
    AdView adView;

    private GridLayoutManager gridLayoutManager;
    private CountriesAdapter countriesAdapter;
    private String countriesJson;


    private Gson gson;
    private ApiServices service;


    private ArrayList<CountryItem> countriesArrayList = new ArrayList<>();


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        service = RetrofitClient.create();
        gson = new Gson();

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        loadingCountriesList();
        return view;

    }

    private void loadingCountriesList() {

        Utils.getInstance(getActivity()).showProgress();

        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Call<ArrayList<CountryItem>> call = service.getCountries();

            call.enqueue(new Callback<ArrayList<CountryItem>>() {
                @Override
                public void onResponse(Call<ArrayList<CountryItem>> call, Response<ArrayList<CountryItem>> response) {
                    if (response.isSuccessful()) {

                        countriesArrayList = response.body();

                        countriesJson = gson.toJson(countriesArrayList);
                        Log.e(TAG, countriesJson);
                        countriesAdapter = new CountriesAdapter(getActivity(), countriesArrayList);
                        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(countriesAdapter);

                        onSuccess();
                    } else {
                        Utils.getInstance(getActivity()).dismissProgress();
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<CountryItem>> call, Throwable t) {
                    Log.e(TAG, "FAIL");
                    onError();
                }

            });


        } else {
            Toast.makeText(getActivity(), this.getResources().getString(R.string.error_network_connection), Toast.LENGTH_LONG).show();

        }
    }


    private void onSuccess() {
        Utils.getInstance(getActivity()).dismissProgress();
    }

    private void onError() {
        Utils.getInstance(getActivity()).dismissProgress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());;
        mFirebaseAnalytics.setCurrentScreen(getActivity(), TAG + " paid", null /* class override */);

    }
}
