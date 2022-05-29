package com.example.task101;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.task101.util.Util;

import java.util.ArrayList;

public class TruckDatabaseHelper extends SQLiteOpenHelper {

    // constructor
    public TruckDatabaseHelper(@Nullable Context context) {
        super(context, Util.TRUCK_DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    // create a new database with SQL commands
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TRUCK_TABLE = "CREATE TABLE " + Util.TRUCK_TABLE_NAME + " ("
                + Util.TRUCK_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + Util.TRUCK_IMAGE
                + " INTEGER , "
                + Util.TRUCK_NAME
                + " TEXT , "
                + Util.TRUCK_DESCRIPTION
                + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_TRUCK_TABLE);
    }

    // called when database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TRUCK_TABLE = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(DROP_TRUCK_TABLE, new String[]{Util.TRUCK_TABLE_NAME});

        onCreate(sqLiteDatabase);
    }

    // create a new row in the database
    public void insertTruck(Truck truck) {

        // get the writable/editable database object
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // put truck data as key-value pair
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.TRUCK_IMAGE, truck.getImage());
        contentValues.put(Util.TRUCK_NAME, truck.getName());
        contentValues.put(Util.TRUCK_DESCRIPTION, truck.getDescription());

        // create a new row with the key-value pairs data
        sqLiteDatabase.insert(Util.TRUCK_TABLE_NAME, null, contentValues);

        // good practice: close the database
        sqLiteDatabase.close();
    }

    // get all trucks in the database
    @SuppressLint("Range")
    public ArrayList<Truck> fetchAllTrucks()
    {
        // create a readable database
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Util.TRUCK_TABLE_NAME, null);

        ArrayList<Truck> truckList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do {
                // create a new truck object for ever row
                Truck truck = new Truck();
                truck.setImage(cursor.getInt(cursor.getColumnIndex(Util.TRUCK_IMAGE)));
                truck.setName(cursor.getString(cursor.getColumnIndex(Util.TRUCK_NAME)));
                truck.setDescription(cursor.getString(cursor.getColumnIndex(Util.TRUCK_DESCRIPTION)));

                truckList.add(truck);

            } while (cursor.moveToNext());
        }

        return truckList;
    }
}
