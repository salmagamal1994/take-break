package com.example.android.take_a_break_app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.take_a_break_app.fragments.MainFragment;
import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.fragments.FavouriteFragment;
import com.example.android.take_a_break_app.helpers.Constants;
import com.example.android.take_a_break_app.helpers.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Menu menu;
    private SharedPreferences sharedPreferences;
    private String sort_type;
    private Bundle args;

    private MainFragment mainFragment;
    private FavouriteFragment favouriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        sort_type = getSortTypeFromPrefrence();
        if (sort_type.equals(Constants.ALL_PLACES)) {

            mainFragment = new MainFragment();
            args = new Bundle();
            args.putString(Constants.SORT_TYPE_KEY, sort_type);
            mainFragment.setArguments(args);
            //Inflate the fragment
            getSupportFragmentManager().beginTransaction().add(R.id.countries_list_container, mainFragment).commit();
            Log.e("sort_type_init", sort_type);
        } else if (sort_type.equals(Constants.FAVOURITE_PLACES)) {
            favouriteFragment = new FavouriteFragment();
            args = new Bundle();
            args.putString(Constants.SORT_TYPE_KEY, sort_type);
            favouriteFragment.setArguments(args);
            //Inflate the fragment
            getSupportFragmentManager().beginTransaction().add(R.id.countries_list_container, favouriteFragment).commit();
            Log.e("sort_type_init", sort_type);
        }

//        Crashlytics.getInstance().crash(); // Force a crash

    }

    private String getSortTypeFromPrefrence() {
        sharedPreferences = getSharedPreferences("my_pref", MODE_PRIVATE);

        return sharedPreferences.getString("sort_type", Constants.ALL_PLACES);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (sort_type.equals(Constants.FAVOURITE_PLACES)) {
            menu.getItem(0).setIcon(R.drawable.ic_action_favourite);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_action_all);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (sharedPreferences == null) {
            getSortTypeFromPrefrence();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        sort_type = sharedPreferences.getString("sort_type", Constants.ALL_PLACES);

        if (id == R.id.action_all_places) {
            menu.getItem(0).setIcon(R.drawable.ic_action_all);
            if (NetworkUtils.isNetworkAvailable(this)) {
                //  sort_type = sharedPreferences.getString("sort_type", Constants.ALL_PLACES);
                editor.putString("sort_type", Constants.ALL_PLACES);
                mainFragment = new MainFragment();
                sort_type = Constants.ALL_PLACES;
                args.putString(Constants.SORT_TYPE_KEY, sort_type);
                mainFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.countries_list_container, mainFragment).commit();

            } else {
                Toast.makeText(this, this.getResources().getString(R.string.error_network_connection), Toast.LENGTH_LONG).show();
            }
            editor.commit();
            Log.e("sort_type_all", sharedPreferences.getString("sort_type", Constants.ALL_PLACES));
            return true;
        } else if (id == R.id.action_favourite_places) {
            menu.getItem(0).setIcon(R.drawable.ic_action_favourite);
            if (NetworkUtils.isNetworkAvailable(this)) {
                // sort_type = sharedPreferences.getString("sort_type", Constants.FAVOURITE_PLACES);
                editor.putString("sort_type", Constants.FAVOURITE_PLACES);
                favouriteFragment = new FavouriteFragment();
                sort_type = Constants.FAVOURITE_PLACES;
                args.putString(Constants.SORT_TYPE_KEY, sort_type);
                favouriteFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.countries_list_container, favouriteFragment).commit();

            } else {
                Toast.makeText(this, this.getResources().getString(R.string.error_network_connection), Toast.LENGTH_LONG).show();
            }
            editor.commit();
            Log.e("sort_type_fav", sharedPreferences.getString("sort_type", Constants.FAVOURITE_PLACES));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

