package com.example.android.take_a_break_app.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.adapters.ThingsToDoAdapter;
import com.example.android.take_a_break_app.data.FavouriteContract;
import com.example.android.take_a_break_app.helpers.NetworkUtils;
import com.example.android.take_a_break_app.helpers.Utils;
import com.example.android.take_a_break_app.models.ThingsToDoItem;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ThingsToDoItem>> {

    private static final String TAG = FavouriteFragment.class.getSimpleName();

    private Unbinder unbinder;
    @BindView(R.id.rv_favourite_list)
    RecyclerView rvFavouriteList;

    private static final int FAVOURITE_LOADER_KEY = 1239;
    private LoaderManager loadermanager;
    private Loader<ArrayList<ThingsToDoItem>> mloader;
    private ArrayList<ThingsToDoItem> mThingsToDoItems = null;
    private JSONArray jsonArray;

    private LinearLayoutManager linearLayoutManager;
    private ThingsToDoAdapter thingsToDoAdapter;

    private Context mcontext;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mcontext = getActivity();
        loadAllFavouritePlaces();
    }


    private void loadAllFavouritePlaces() {
        /*
        Check if there is an internet connection. If so, request movies data
        with the current search criteria. Else, display a "No Connection" dialog
         */
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            // Loader Manager

            loadermanager = getLoaderManager();
            mloader = loadermanager.getLoader(FAVOURITE_LOADER_KEY);
            Bundle b = new Bundle();
            if (mloader == null) {
                mloader = loadermanager.initLoader(FAVOURITE_LOADER_KEY, b, this);
            } else {
                mloader = loadermanager.restartLoader(FAVOURITE_LOADER_KEY, b, this);
            }

        } else {
            Toast.makeText(getActivity(), this.getResources().getString(R.string.error_network_connection), Toast.LENGTH_LONG).show();
        }
    }

    private void setMainActivityLayoutManager() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFavouriteList.setLayoutManager(linearLayoutManager);

    }

    private void setMainActivityAdapter(ArrayList<ThingsToDoItem> favouriteItems) {

        // Layout Manager
        setMainActivityLayoutManager();

        // Create and set the adapter
        thingsToDoAdapter = new ThingsToDoAdapter(getActivity(), favouriteItems);


        if (favouriteItems.size() > 0) {
            rvFavouriteList.setAdapter(thingsToDoAdapter);
        }
    }


    @Override
    public Loader<ArrayList<ThingsToDoItem>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<ThingsToDoItem>>(mcontext) {

            ArrayList<ThingsToDoItem> myFavourites;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (NetworkUtils.isNetworkAvailable(mcontext)) {
                    if (mThingsToDoItems == null && Utils.getInstance(getActivity()).getProgressDialog() != null) {
                        Utils.getInstance(getActivity()).showProgress();
                    }
                    forceLoad();

                } else {
                    Toast.makeText(mcontext, mcontext.getResources().getString(R.string.error_network_connection), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void deliverResult(ArrayList<ThingsToDoItem> data) {
                this.myFavourites = data;
                Utils.getInstance(getActivity()).dismissProgress();
                super.deliverResult(data);
            }


            @Override
            public ArrayList<ThingsToDoItem> loadInBackground() {
                myFavourites = new ArrayList<ThingsToDoItem>();
                try {
                    return parseFavCursor(mcontext.getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null));

                } catch (Exception e) {
                    Log.e("loadingFailed", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ThingsToDoItem>> loader, ArrayList<ThingsToDoItem> thingsToDoItems) {
        Utils.getInstance(getActivity()).dismissProgress();

        if (thingsToDoAdapter == null)
            setMainActivityAdapter(thingsToDoItems);
        else
            thingsToDoAdapter.setThingsToDoItems(thingsToDoItems);

        if (thingsToDoItems.size() == 0) {
            Toast.makeText(getActivity(), "there is no data to show", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ThingsToDoItem>> loader) {

    }

    private ArrayList<ThingsToDoItem> parseFavCursor(Cursor query) {
        jsonArray = null;
        ArrayList<ThingsToDoItem> favourites = new ArrayList<ThingsToDoItem>();
        while (query.moveToNext()) {
            ThingsToDoItem m = new ThingsToDoItem();

            m.setId(Integer.parseInt(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_PLACE_KEY))));
            m.setPlaceLogo(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_PLACE_LOGO)));
            m.setShortDescription(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_PLACE_TITLE)));
            m.setAddress(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_ADDRESS)));
            m.setOpeningHours(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_OPENING_HOURS)));
            m.setWebsite(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_WEBSITE)));
            m.setDescription(query.getString(query.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_DESCRIPTION)));

            favourites.add(m);
        }
        mThingsToDoItems = favourites;
        return favourites;
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
