package com.example.android.take_a_break_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.activities.MainActivity;
import com.example.android.take_a_break_app.fragments.ThingsToDoFragment;
import com.example.android.take_a_break_app.helpers.Constants;
import com.example.android.take_a_break_app.models.CountryItem;
import com.example.android.take_a_break_app.models.ThingsToDoItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by salma gamal on 19/10/2018.
 */

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesViewHolder> {


    private ArrayList<CountryItem> countryItems;
    private Context context;
    private Gson gson;

    private ArrayList<ThingsToDoItem> thingsToDoItems;
    FirebaseAnalytics mFirebaseAnalytics ;

    public CountriesAdapter(Context context, ArrayList<CountryItem> countryItems) {
        this.context = context;
        this.countryItems = countryItems;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context) ;


    }

    @Override
    public CountriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_country, parent, false);
        return new CountriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountriesViewHolder holder, int position) {
        final CountryItem countryItem = countryItems.get(position);
        CountriesViewHolder countriesViewHolder = holder;

        countriesViewHolder.countryName.setText(countryItem.getName());

        String imageUrl = countryItem.getCountryLogo();
        Log.e("imageUrl", imageUrl);
        if (!imageUrl.equals("")) {
            //Load image if present
            Picasso.with(context).load(imageUrl).into(holder.thumbnail);
        }


    }

    @Override
    public int getItemCount() {
        return countryItems.size();
    }

    public class CountriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnail;

        @BindView(R.id.country_name)
        TextView countryName;

        public CountriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recordCountrySelected(countryItems.get(getAdapterPosition()));
                    thingsToDoItems = countryItems.get(getAdapterPosition()).getThingsToDoItems();
                    //Put the value
                    ThingsToDoFragment thingsToDoFragment = new ThingsToDoFragment();
                    Bundle args = new Bundle();
                    gson = new Gson();
                    String thingsToDoJson = gson.toJson(thingsToDoItems);
                    args.putString(Constants.THINGS_TO_DO_KEY, thingsToDoJson);
                    thingsToDoFragment.setArguments(args);
                    //Inflate the fragment
                    ((MainActivity) context).getSupportFragmentManager().beginTransaction().addToBackStack(thingsToDoFragment.getTag()).add(R.id.countries_list_container, thingsToDoFragment).commit();
                }
            });
        }

        private void recordCountrySelected(CountryItem countryItem) {
            String id =  String.valueOf(countryItem.getId());
            String name = countryItem.getName();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "country");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }


}
