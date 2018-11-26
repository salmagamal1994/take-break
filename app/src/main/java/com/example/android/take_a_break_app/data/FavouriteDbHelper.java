package com.example.android.take_a_break_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavouriteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "favourite.db";

    public FavouriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE if not exists " + FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +

                FavouriteContract.FavouriteEntry.COLUMN_PLACE_KEY + " INTEGER PRIMARY KEY, " +
                FavouriteContract.FavouriteEntry.COLUMN_PLACE_TITLE + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_PLACE_LOGO + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_WEBSITE + " TEXT NOT NULL," +
                FavouriteContract.FavouriteEntry.COLUMN_OPENING_HOURS + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                " UNIQUE (" + FavouriteContract.FavouriteEntry.COLUMN_PLACE_KEY + ") ON CONFLICT IGNORE " + "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteContract.FavouriteEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);


    }
}
