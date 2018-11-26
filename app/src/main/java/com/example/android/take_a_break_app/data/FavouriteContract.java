package com.example.android.take_a_break_app.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Copy Center on 14/10/2018.
 */

public class FavouriteContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.take_a_break_app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITE = "favourite";

    public static final class FavouriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favourite";


        public static final String COLUMN_PLACE_KEY = "place_id";
        public static final String COLUMN_PLACE_LOGO = "place_logo";
        public static final String COLUMN_PLACE_TITLE = "short_description";
        public static final String COLUMN_OPENING_HOURS = "opening_hours";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_WEBSITE = "Website";
        public static final String COLUMN_DESCRIPTION = "description";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();

        public static Uri buildFavouriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
