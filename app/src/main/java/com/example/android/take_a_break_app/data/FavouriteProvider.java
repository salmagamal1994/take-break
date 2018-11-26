package com.example.android.take_a_break_app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by salma gamal on 23/11/2018.
 */

public class FavouriteProvider extends ContentProvider {

    private FavouriteDbHelper mFavouritesOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int FAVOURITE_PLACES_LIST = 100;
    static final int FAVOURITE_PLACE_ITEM = 101;


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavouriteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavouriteContract.PATH_FAVOURITE, FAVOURITE_PLACES_LIST);
        matcher.addURI(authority, FavouriteContract.PATH_FAVOURITE + "/*", FAVOURITE_PLACE_ITEM);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mFavouritesOpenHelper = new FavouriteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case FAVOURITE_PLACES_LIST: {
                retCursor = mFavouritesOpenHelper.getReadableDatabase().query(
                        FavouriteContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }

            case FAVOURITE_PLACE_ITEM: {
                retCursor = mFavouritesOpenHelper.getReadableDatabase().query(
                        FavouriteContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFavouritesOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVOURITE_PLACES_LIST: {
                long _id = db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FavouriteContract.FavouriteEntry.buildFavouriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = mFavouritesOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;


        switch (match) {
            case FAVOURITE_PLACE_ITEM:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(FavouriteContract.FavouriteEntry.TABLE_NAME, "place_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
