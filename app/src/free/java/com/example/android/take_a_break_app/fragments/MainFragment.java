package com.example.android.take_a_break_app.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.take_a_break_app.R;
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
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();

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


    //Scroll state
    private Parcelable mState;
    public static final String BUNDLE_COUNTRIES_ARRAY_KEY = "countries";
    public static final String BUNDLE_GRID_SCROLL_KEY = "gridScroll";
    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";


    private ArrayList<CountryItem> mCountriesArray = null;
    private String mCountriesJson;
    private String mCountriesRecievedFromInstance;
    private Parcelable mLayoutManagerState;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check if there is a previous state to be restored
        if (savedInstanceState == null
                || !savedInstanceState.containsKey(BUNDLE_COUNTRIES_ARRAY_KEY)
                || !savedInstanceState.containsKey(BUNDLE_GRID_SCROLL_KEY)) {
        } else {
            //Retrieve data from the previous state
            mCountriesRecievedFromInstance = savedInstanceState.getString(BUNDLE_COUNTRIES_ARRAY_KEY);
            gson = new Gson();
            mCountriesArray = gson.fromJson(mCountriesRecievedFromInstance, new TypeToken<ArrayList<CountryItem>>() {
            }.getType());
            // Prevent cases where there was no internet connection,
            // no data was loaded previously but the user rotates device
            if (mCountriesArray != null) {
                setMainFragmentAdapter(mCountriesArray);
                restoreScrollPosition(savedInstanceState);
            }
        }

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
                        mCountriesArray = countriesArrayList;

                        countriesJson = gson.toJson(countriesArrayList);
                        Log.e(TAG, countriesJson);

                        if (countriesAdapter == null)
                            setMainFragmentAdapter(countriesArrayList);
                        else
                            countriesAdapter.setCountriesData(countriesArrayList);

//                        countriesAdapter = new CountriesAdapter(getActivity(), countriesArrayList);
//                        int columnNumber = CountriesAdapter.calculateColumns(getActivity());
//                        gridLayoutManager = new GridLayoutManager(getActivity(), columnNumber);
//                        recyclerView.setLayoutManager(gridLayoutManager);
//                        recyclerView.setAdapter(countriesAdapter);

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
            Utils.getInstance(getActivity()).dismissProgress();
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
    public void onPause() {
        super.onPause();
        // To restore RecyclerView scroll
        if(recyclerView !=null && gridLayoutManager !=null) {
            mState = gridLayoutManager.onSaveInstanceState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setCurrentScreen(getActivity(), TAG + " free", null /* class override */);

        if (mCountriesArray == null) {
            loadingCountriesList();
        } else {
            setMainFragmentAdapter(mCountriesArray);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        mCountriesJson = gson.toJson(mCountriesArray);
        // Insert data into the Bundle
        outState.putString(BUNDLE_COUNTRIES_ARRAY_KEY, mCountriesJson);
        // If the view was loaded correctly
        if (recyclerView != null) {
            outState.putInt(BUNDLE_GRID_SCROLL_KEY, gridLayoutManager.findFirstVisibleItemPosition());
            outState.putParcelable(LAYOUT_MANAGER_STATE, mLayoutManagerState);
        }

        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    private void restoreScrollPosition(Bundle savedInstanceState) {
        int position = savedInstanceState.getInt(BUNDLE_GRID_SCROLL_KEY);
        mLayoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
        recyclerView.smoothScrollToPosition(position);
        recyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
    }


    private void setMainFragmentLayoutManager() {
        // Dynamically calculate the number of columns the GridManager should create
        // depending on the screen size
        int numberOfColumns = CountriesAdapter.calculateColumns(getActivity());

        // Create and apply the layout manager
        gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void setMainFragmentAdapter(ArrayList<CountryItem> countryItems) {

        // Layout Manager
        setMainFragmentLayoutManager();

        // Create and set the adapter
        countriesAdapter = new CountriesAdapter(getActivity(), countryItems);

        if (countryItems.size() > 0) {
            recyclerView.setAdapter(countriesAdapter);
        }
    }


}
