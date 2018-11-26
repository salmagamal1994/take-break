package com.example.android.take_a_break_app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.adapters.ThingsToDoAdapter;
import com.example.android.take_a_break_app.helpers.Constants;
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


    public ThingsToDoFragment() {
        // Required empty public constructor
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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        thingsToDoList.setLayoutManager(linearLayoutManager);
        thingsToDoAdapter = new ThingsToDoAdapter(getActivity(), thingsToDoItems);
        thingsToDoList.setAdapter(thingsToDoAdapter);

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
        mFirebaseAnalytics.setCurrentScreen(getActivity(), TAG , null /* class override */);

    }
}
