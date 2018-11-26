package com.example.android.take_a_break_app.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.helpers.Constants;
import com.example.android.take_a_break_app.models.ThingsToDoItem;
import com.example.android.take_a_break_app.widget.PlaceWidgetProvider;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.place_title)
    TextView placeTitle;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.address_layout)
    LinearLayout addressLayout;
    @BindView(R.id.tv_opening_hours)
    TextView tvOpeningHours;
    @BindView(R.id.opening_hours_layout)
    LinearLayout openingHoursLayout;
    @BindView(R.id.tv_website)
    TextView tvWebsite;
    @BindView(R.id.website_layout)
    LinearLayout websiteLayout;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.fab_widget)
    FloatingActionButton fabWidget;


    private String thingsToDoOneItemJson = "";
    private ThingsToDoItem thingsToDoItem;
    private Gson gson;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (getIntent().getStringExtra(Constants.THINGS_TO_DO_ONE_ITEM_KEY) != null) {
            thingsToDoOneItemJson = getIntent().getStringExtra(Constants.THINGS_TO_DO_ONE_ITEM_KEY);
            gson = new Gson();
            thingsToDoItem = gson.fromJson(thingsToDoOneItemJson, ThingsToDoItem.class);
        }

        showDetailsData();

        fabWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                ThingsToDoItem thingsToDoItem = gson.fromJson(sharedPreferences.getString(Constants.WIDGET_RESULT, null), ThingsToDoItem.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(DetailsActivity.this);
                Bundle bundle = new Bundle();
                int appWidgetId = bundle.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                PlaceWidgetProvider.updateAppWidget(DetailsActivity.this, appWidgetManager, appWidgetId, thingsToDoItem.getShortDescription(),thingsToDoItem);
                Toast.makeText(getApplicationContext(), "Added " + thingsToDoItem.getShortDescription() + " to Widget.", Toast.LENGTH_SHORT).show();

                mFirebaseAnalytics.setUserProperty("widget", thingsToDoItem.getShortDescription());

            }
        });


    }

    private void showDetailsData() {
        String imageUrl = thingsToDoItem.getPlaceLogo();
        if (!imageUrl.equals("")) {
            Picasso.with(this).load(imageUrl).into(photo);
        }
        if (thingsToDoItem.getShortDescription() != null) {
            placeTitle.setText(thingsToDoItem.getShortDescription());
        }

        if (thingsToDoItem.getAddress() != null) {
            tvAddress.setText(thingsToDoItem.getAddress());
        }

        if (thingsToDoItem.getOpeningHours() != null) {
            tvOpeningHours.setText(thingsToDoItem.getOpeningHours());
        }

        if (thingsToDoItem.getWebsite() != null) {
            tvWebsite.setText(thingsToDoItem.getWebsite());
        }

        if (thingsToDoItem.getDescription() != null) {
            tvDescription.setText(thingsToDoItem.getDescription());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, thingsToDoItem.getShortDescription() + " details" , null /* class override */);

    }
}
