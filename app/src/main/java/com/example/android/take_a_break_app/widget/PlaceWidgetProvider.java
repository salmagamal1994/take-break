package com.example.android.take_a_break_app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.android.take_a_break_app.R;
import com.example.android.take_a_break_app.activities.DetailsActivity;
import com.example.android.take_a_break_app.activities.MainActivity;
import com.example.android.take_a_break_app.helpers.Constants;
import com.example.android.take_a_break_app.models.ThingsToDoItem;
import com.google.gson.Gson;

/**
 * Implementation of App Widget functionality.
 */
public class PlaceWidgetProvider extends AppWidgetProvider {
    SharedPreferences sharedPreferences;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String placeName, ThingsToDoItem thingsToDoItem) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.place_widget_provider);

        String placeAddress = thingsToDoItem.getAddress();
        String placeWebsite = thingsToDoItem.getWebsite();
        String placeOpenHours = thingsToDoItem.getOpeningHours();

        views.setTextViewText(R.id.place_name_text_view, placeName);
        views.setTextViewText(R.id.address_text, placeAddress);
        views.setTextViewText(R.id.website_text, placeWebsite);
        views.setTextViewText(R.id.open_hours_text, placeOpenHours);


        //Create an Intent with the AppWidgetManager.ACTION_APPWIDGET_UPDATE action//

        Intent intentUpdate = new Intent(context, PlaceWidgetProvider.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);


       //Update the current widget instance only, by creating an array that contains the widget’s unique ID//

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);


//      Wrap the intent as a PendingIntent, using PendingIntent.getBroadcast()//

        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.button_update, pendingUpdate);

        Gson gson = new Gson();
        String thingsToDoOneItemJson = gson.toJson(thingsToDoItem);
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(Constants.THINGS_TO_DO_ONE_ITEM_KEY, thingsToDoOneItemJson);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);


        // Instruct the widget manager to update the widget

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(Constants.WIDGET_RESULT, null);
        Gson gson = new Gson();
        ThingsToDoItem placeItem = gson.fromJson(result, ThingsToDoItem.class);
        String placeName = placeItem.getShortDescription();
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, placeName, placeItem);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

