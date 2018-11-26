package com.example.android.take_a_break_app.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.activities.DetailsActivity;
import com.example.android.take_a_break_app.activities.MainActivity;
import com.example.android.take_a_break_app.data.FavouriteContract;
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
 * Created by salma gamal on 18/11/2018.
 */

public class ThingsToDoAdapter extends RecyclerView.Adapter<ThingsToDoAdapter.ThingsToDoViewHolder> {


    private final FirebaseAnalytics mFirebaseAnalytics;
    private ArrayList<ThingsToDoItem> thingsToDoItems;
    private Context context;
    private Intent intent;
    private Gson gson;

    private SharedPreferences sharedPreferences;

    private ThingsToDoItem thingsToDoOneItem;

    public ThingsToDoAdapter(Context context, ArrayList<ThingsToDoItem> thingsToDoItems) {
        this.context = context;
        this.thingsToDoItems = thingsToDoItems;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context) ;
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public ThingsToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_things_to_do, parent, false);
        return new ThingsToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ThingsToDoViewHolder holder, final int position) {
        final ThingsToDoItem thingsToDoItem = thingsToDoItems.get(position);
        final ThingsToDoViewHolder thingsToDoViewHolder = holder;

        thingsToDoViewHolder.shortDescription.setText(thingsToDoItem.getShortDescription());

        String imageUrl = thingsToDoItem.getPlaceLogo();
        Log.e("imageUrl", imageUrl);
        if (!imageUrl.equals("")) {
            //Load image if present
            Picasso.with(context).load(imageUrl).into(holder.thumbnail);
        }

        if (isExist(String.valueOf(thingsToDoItem.getId()))) {
            thingsToDoViewHolder.ivAddToFav.setButtonDrawable(R.drawable.ic_action_favourite);
            thingsToDoViewHolder.ivAddToFav.setChecked(true);
        } else {
            thingsToDoViewHolder.ivAddToFav.setButtonDrawable(R.drawable.ic_action_not_favourite_place);
            thingsToDoViewHolder.ivAddToFav.setChecked(false);
        }


        thingsToDoViewHolder.ivAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thingsToDoViewHolder.ivAddToFav.isChecked()) {
                    thingsToDoViewHolder.ivAddToFav.setButtonDrawable(R.drawable.ic_action_favourite);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_PLACE_KEY, thingsToDoItem.getId());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_PLACE_LOGO, thingsToDoItem.getPlaceLogo());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_PLACE_TITLE, thingsToDoItem.getShortDescription());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_OPENING_HOURS, thingsToDoItem.getOpeningHours());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_ADDRESS, thingsToDoItem.getAddress());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_WEBSITE, thingsToDoItem.getWebsite());
                    contentValues.put(FavouriteContract.FavouriteEntry.COLUMN_DESCRIPTION, thingsToDoItem.getDescription());
                    Uri uri = ((MainActivity) context).getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, contentValues);
                    if (uri != null) {
                        Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
                    }
                    mFirebaseAnalytics.setUserProperty("favorite_place", thingsToDoItem.getShortDescription());
                } else {
                    thingsToDoViewHolder.ivAddToFav.setButtonDrawable(R.drawable.ic_action_not_favourite_place);
                    Uri uri = FavouriteContract.FavouriteEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(String.valueOf(thingsToDoItem.getId())).build();
                    int p = context.getContentResolver().delete(uri, null, null);
                    if (p > 0) {
                        removeAt(position);
                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return thingsToDoItems.size();
    }

    public class ThingsToDoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnail;

        @BindView(R.id.country_name)
        TextView shortDescription;

        @BindView(R.id.iv_add_to_fav)
        CheckBox ivAddToFav;

        public ThingsToDoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    thingsToDoOneItem = thingsToDoItems.get(getAdapterPosition());
                    recordToDoSelected(thingsToDoOneItem);
                    Gson gson = new Gson();
                    String thingsToDoOneItemJson = gson.toJson(thingsToDoOneItem);
                    //Put the value
                    intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(Constants.THINGS_TO_DO_ONE_ITEM_KEY, thingsToDoOneItemJson);
                    sharedPreferences.edit().putString(Constants.WIDGET_RESULT, thingsToDoOneItemJson).apply();
                    context.startActivity(intent);
                }
            });
        }

        private void recordToDoSelected(ThingsToDoItem item) {
            String id =  String.valueOf(item.getId());
            String name = item.getShortDescription();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Things to-do");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    public void setThingsToDoItems(ArrayList<ThingsToDoItem> thingsToDoItems) {
        this.thingsToDoItems = thingsToDoItems;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        thingsToDoItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, thingsToDoItems.size());
    }

    private boolean isExist(String placeId) {
        String selection = FavouriteContract.FavouriteEntry.COLUMN_PLACE_KEY + " = '"
                + placeId + "'";

        return context.getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI,
                null,
                selection,
                null,
                null).getCount() > 0 ? true : false;
    }
}
