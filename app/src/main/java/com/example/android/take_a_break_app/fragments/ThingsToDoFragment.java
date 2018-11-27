package com.example.android.take_a_break_app.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.adapters.CountriesAdapter;
import com.example.android.take_a_break_app.adapters.ThingsToDoAdapter;
import com.example.android.take_a_break_app.helpers.Constants;
import com.example.android.take_a_break_app.models.CountryItem;
import com.example.android.take_a_break_app.models.ThingsToDoItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ThingsToDoFragment extends Fragment {
    private static final String TAG = ThingsToDoFragment.class.getSimpleName();


    private Unbinder unbinder;

    @BindView(R.id.things_to_do_list)
    RecyclerView thingsToDoList;


    private LinearLayoutManager linearLayoutManager;
    private ThingsToDoAdapter thingsToDoAdapter;

    private String thingsToDoJson = "";
    private Gson gson;
    private ArrayList<ThingsToDoItem> thingsToDoItems;

    //Scroll state
    private Parcelable mState;
    public static final String BUNDLE_THINGS_TO_DO_ARRAY_KEY = "places";
    public static final String BUNDLE_SCROLL_KEY = "linearScroll";

    private ArrayList<ThingsToDoItem> mthingsToDoItemsForSavedInstance = null;
    private String mThingsToDoItemJson;
    private String mThingsToDoItemRecievedFromInstance;


    public ThingsToDoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check if there is a previous state to be restored
        if (savedInstanceState == null
                || !savedInstanceState.containsKey(BUNDLE_THINGS_TO_DO_ARRAY_KEY)
                || !savedInstanceState.containsKey(BUNDLE_SCROLL_KEY)) {
        } else {
            //Retrieve data from the previous state
            mThingsToDoItemRecievedFromInstance = savedInstanceState.getString(BUNDLE_THINGS_TO_DO_ARRAY_KEY);
            gson = new Gson();
            mthingsToDoItemsForSavedInstance = gson.fromJson(mThingsToDoItemRecievedFromInstance, new TypeToken<ArrayList<ThingsToDoItem>>() {
            }.getType());
            // Prevent cases where there was no internet connection,
            // no data was loaded previously but the user rotates device
            if (mthingsToDoItemsForSavedInstance != null) {
                setToDoAdapter(mthingsToDoItemsForSavedInstance);
                restoreScrollPosition(savedInstanceState);
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString(Constants.THINGS_TO_DO_KEY) != null) {
                thingsToDoJson = bundle.getString(Constants.THINGS_TO_DO_KEY);
                gson = new Gson();
                thingsToDoItems = gson.fromJson(thingsToDoJson, new TypeToken<ArrayList<ThingsToDoItem>>() {
                }.getType());
                mthingsToDoItemsForSavedInstance = thingsToDoItems;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_things_to_do, container, false);
        unbinder = ButterKnife.bind(this, view);

        loadThingsToDoList();

        return view;

    }

    private void loadThingsToDoList() {
        if (thingsToDoAdapter == null)
            setToDoAdapter(thingsToDoItems);
        else
            thingsToDoAdapter.setToDoData(thingsToDoItems);


//        linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        thingsToDoList.setLayoutManager(linearLayoutManager);
//        thingsToDoAdapter = new ThingsToDoAdapter(getActivity(), thingsToDoItems);
//        thingsToDoList.setAdapter(thingsToDoAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        mState = linearLayoutManager.onSaveInstanceState();

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setCurrentScreen(getActivity(), TAG, null /* class override */);

        if (mthingsToDoItemsForSavedInstance == null) {
            loadThingsToDoList();
        } else {
            setToDoAdapter(mthingsToDoItemsForSavedInstance);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        mThingsToDoItemJson = gson.toJson(mthingsToDoItemsForSavedInstance);
        // Insert data into the Bundle
        outState.putString(BUNDLE_THINGS_TO_DO_ARRAY_KEY, mThingsToDoItemJson);
        // If the view was loaded correctly
        if (thingsToDoList != null) {
            outState.putInt(BUNDLE_SCROLL_KEY, linearLayoutManager.findFirstVisibleItemPosition());
        }

        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    private void restoreScrollPosition(Bundle savedInstanceState) {
        int position = savedInstanceState.getInt(BUNDLE_SCROLL_KEY);
        thingsToDoList.smoothScrollToPosition(position);
    }


    private void setToDoLayoutManager() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        thingsToDoList.setLayoutManager(linearLayoutManager);

    }

    private void setToDoAdapter(ArrayList<ThingsToDoItem> items) {

        // Layout Manager
        setToDoLayoutManager();

        // Create and set the adapter
        thingsToDoAdapter = new ThingsToDoAdapter(getActivity(), items);


        if (items.size() > 0) {
            thingsToDoList.setAdapter(thingsToDoAdapter);
        }
    }
}
