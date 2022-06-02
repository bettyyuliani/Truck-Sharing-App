package com.example.task82;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.task82.util.Util;

import java.util.ArrayList;

public class OrderDatabaseHelper extends SQLiteOpenHelper {

    // constructor for database helper object
    public OrderDatabaseHelper(@Nullable Context context) {
        super(context, Util.ORDER_DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    // create the database with SQL commands
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ORDER_TABLE = "CREATE TABLE "
                + Util.ORDER_TABLE_NAME
                + " ("
                + Util.ORDER_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + Util.USERNAME + " TEXT , "
                + Util.GOOD_IMAGE + " BLOB , "
                + Util.RECEIVER_NAME + " TEXT , "
                + Util.DATE + " TEXT , "
                + Util.TIME + " TEXT , "
                + Util.LOCATION + " TEXT , "
                + Util.LOCATION_LATITUDE + " REAL , "
                + Util.LOCATION_LONGITUDE + " REAL , "
                + Util.DESTINATION+ " TEXT , "
                + Util.DESTINATION_LATITUDE + " REAL , "
                + Util.DESTINATION_LONGITUDE + " REAL , "
                + Util.GOOD_TYPE + " TEXT , "
                + Util.VEHICLE_TYPE + " TEXT , "
                + Util.WEIGHT + " TEXT , "
                + Util.LENGTH + " TEXT , "
                + Util.HEIGHT + " TEXT , "
                + Util.WIDTH + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_ORDER_TABLE);
    }

    // update specific usernames in the table
    public void updateUsername(String oldUsername, ContentValues contentValues)
    {
        // an editable database object
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.update(Util.ORDER_TABLE_NAME, contentValues, Util.USERNAME + "=?", new String[] {oldUsername});
    }

    // called when database needs to be updated
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_ORDER_TABLE = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(DROP_ORDER_TABLE, new String[]{Util.ORDER_TABLE_NAME});

        onCreate(sqLiteDatabase);
    }

    public int getOrderCount(String username)
    {
        // get the readable database object
        SQLiteDatabase db = this.getReadableDatabase();

        // get all orders from the table which belongs to the logged in user
        Cursor cursor = db.rawQuery("SELECT * FROM " + Util.ORDER_TABLE_NAME + " WHERE " + Util.USERNAME + "=?", new String[] {username});

        Integer count = cursor.getCount();
        return count;
    }

    public long insertOrder(Order order) {

        // an editable database object
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // put order data as key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USERNAME, order.getUsername());
        contentValues.put(Util.GOOD_IMAGE, order.getGoodImageByte());
        contentValues.put(Util.RECEIVER_NAME, order.getReceiverName());
        contentValues.put(Util.DATE, order.getDate());
        contentValues.put(Util.TIME, order.getTime());
        contentValues.put(Util.LOCATION, order.getLocation());
        contentValues.put(Util.LOCATION_LATITUDE, order.getLocationLatitude());
        contentValues.put(Util.LOCATION_LONGITUDE, order.getLocationLongitude());
        contentValues.put(Util.DESTINATION, order.getDestination());
        contentValues.put(Util.DESTINATION_LATITUDE, order.getDestinationLatitude());
        contentValues.put(Util.DESTINATION_LONGITUDE, order.getDestinationLongitude());
        contentValues.put(Util.GOOD_TYPE, order.getGoodType());
        contentValues.put(Util.VEHICLE_TYPE, order.getVehicleType());
        contentValues.put(Util.WEIGHT, order.getWeight());
        contentValues.put(Util.LENGTH, order.getLength());
        contentValues.put(Util.HEIGHT, order.getHeight());
        contentValues.put(Util.WIDTH, order.getWidth());

        // create a new row in the database with the data
        long rowId = sqLiteDatabase.insert(Util.ORDER_TABLE_NAME, null, contentValues);
        // good practice: close database
        sqLiteDatabase.close();
        return rowId;
    }

    @SuppressLint("Range")
    public ArrayList<Order> fetchAllOrders(String username)
    {
        // get the readable database object
        SQLiteDatabase db = this.getReadableDatabase();

        // get all orders from the table which belongs to the logged in user
        Cursor cursor = db.rawQuery("SELECT * FROM " + Util.ORDER_TABLE_NAME + " WHERE " + Util.USERNAME + "=?", new String[] {username});

        // a list of orders
        ArrayList<Order> orderList = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do {

                // create new order object with the data obtained from the database table
                Order order = new Order();
                order.setUsername(cursor.getString(cursor.getColumnIndex(Util.USERNAME)));
                order.setGoodImageByte(cursor.getBlob(cursor.getColumnIndex(Util.GOOD_IMAGE)));
                order.setReceiverName(cursor.getString(cursor.getColumnIndex(Util.RECEIVER_NAME)));
                order.setDate(cursor.getString(cursor.getColumnIndex(Util.DATE)));
                order.setTime(cursor.getString(cursor.getColumnIndex(Util.TIME)));
                order.setLocation(cursor.getString(cursor.getColumnIndex(Util.LOCATION)));
                order.setLocationLatitude(cursor.getDouble(cursor.getColumnIndex(Util.LOCATION_LATITUDE)));
                order.setLocationLongitude(cursor.getDouble(cursor.getColumnIndex(Util.LOCATION_LONGITUDE)));
                order.setDestination(cursor.getString(cursor.getColumnIndex(Util.DESTINATION)));
                order.setDestinationLatitude(cursor.getDouble(cursor.getColumnIndex(Util.DESTINATION_LATITUDE)));
                order.setDestinationLongitude(cursor.getDouble(cursor.getColumnIndex(Util.DESTINATION_LONGITUDE)));
                order.setGoodType(cursor.getString(cursor.getColumnIndex(Util.GOOD_TYPE)));
                order.setVehicleType(cursor.getString(cursor.getColumnIndex(Util.VEHICLE_TYPE)));
                order.setWeight(cursor.getString(cursor.getColumnIndex(Util.WEIGHT)));
                order.setHeight(cursor.getString(cursor.getColumnIndex(Util.HEIGHT)));
                order.setLength(cursor.getString(cursor.getColumnIndex(Util.LENGTH)));
                order.setWidth(cursor.getString(cursor.getColumnIndex(Util.WIDTH)));

                orderList.add(order);

            } while (cursor.moveToNext());
        }

        return orderList;
    }

}
