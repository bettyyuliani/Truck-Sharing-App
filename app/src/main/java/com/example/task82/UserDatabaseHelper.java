package com.example.task82;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.task82.util.Util;


public class UserDatabaseHelper extends SQLiteOpenHelper {

    // constructor
    public UserDatabaseHelper(@Nullable Context context) {
        super(context, Util.USER_DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    // create database with SQL commands
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Util.USER_TABLE_NAME + " ("
                + Util.USER_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + Util.PROFILE_PICTURE
                + " BLOB , "
                + Util.NAME
                + " TEXT , "
                + Util.USERNAME
                + " TEXT , "
                + Util.PASSWORD
                + " TEXT , "
                + Util.PHONE_NO
                + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    // called when database needs to be updated
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_USER_TABLE = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(DROP_USER_TABLE, new String[]{Util.USER_TABLE_NAME});

        onCreate(sqLiteDatabase);
    }

    public long insertUser(User user) {

        // create an object for writable/editable database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.PROFILE_PICTURE, user.getImage());
        contentValues.put(Util.NAME, user.getName());
        contentValues.put(Util.USERNAME, user.getUsername());
        contentValues.put(Util.PASSWORD, user.getPassword());
        contentValues.put(Util.PHONE_NO, user.getPhoneNo());

        // create a new row in the database
        long rowId = sqLiteDatabase.insert(Util.USER_TABLE_NAME, null, contentValues);
//        sqLiteDatabase.close();

        return rowId;
    }

    // get user based on the unique username
    @SuppressLint("Range")
    public User getUser(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Util.USER_TABLE_NAME + " WHERE " + Util.USERNAME + "=?", new String[]{username});
        Log.i("tes",cursor.getCount()+"");
        User user = new User();
        if(cursor.moveToFirst()){
            do{
                Log.i("tes", "tes");
                user.setImage(cursor.getBlob(cursor.getColumnIndex(Util.PROFILE_PICTURE)));
                user.setName(cursor.getString(cursor.getColumnIndex(Util.NAME)));
                user.setUsername(username);
                user.setPassword(cursor.getString(cursor.getColumnIndex(Util.PASSWORD)));
                user.setPhoneNo(cursor.getString(cursor.getColumnIndex(Util.PHONE_NO)));

            } while(cursor.moveToNext());
        }
        return user;
    }

    // check if the username exists in the database
    // as it should be unique across all users
    public boolean userExists(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Util.USER_TABLE_NAME, new String[]{Util.USER_ID}, Util.USERNAME + "=?", new String[]{username}, null, null, null);
        int numrows = cursor.getCount();
        db.close();

        if(numrows>0) {
            return true;
        }
        else {
            return false;
        }
    }

    // get the user given their username and password
    public boolean fetchUser(String username, String password) {

        //NOTE: get-READABLE-Database
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(
                Util.USER_TABLE_NAME, //Table name
                new String[]{Util.USER_ID}, //Primary key column
                Util.USERNAME + "=? and " + Util.PASSWORD + "=?", //Columns to be identified
                new String[]{username, password}, //Values of the columns to match
                null, //Grouping of the rows
                null, //Filtering of the grows
                null); //Sorting of the order

        int numberOfRows = cursor.getCount();

        sqLiteDatabase.close();

        if (numberOfRows > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    // used to return an object of writable/editable database
    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    // used to return an object of readable database
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    // function to update the details of the username, given their username
    public int updateDetails(String username, ContentValues contentValues)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.update(Util.USER_TABLE_NAME, contentValues, Util.USERNAME + "=?", new String[] {username});
        return res;
    }

}

