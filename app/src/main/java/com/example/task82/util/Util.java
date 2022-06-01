package com.example.task82.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Util {

   public static final int PICK_LOCATION_REQUEST = 200;
    public static final int PICK_DESTINATION_REQUEST = 300;

    // messages nodes
   public static final String MESSAGE = "message";
   public static final String MESSAGE_ID = "message_ID";
   public static final String MESSAGE_TIME = "message_time";
   public static final String MESSAGE_SENDER = "message_sender";
   public static final String MESSAGES = "messages";
    // api key
    public static final String API_KEY = "AIzaSyDg3rrIKggl9CmPicsSMJGlX-814PSsdg8";
    public static final String LOCATION_LATITUDE = "location_latitude";
    public static final String LOCATION_LONGITUDE = "location_longitude";
    public static final String DESTINATION_LATITUDE = "destination_latitude";
    public static final String DESTINATION_LONGITUDE = "destination_longitude";
    // variable used across all database
    public static final int DATABASE_VERSION = 1;

    //variables for shared preferences
    public static final String LOGGEDIN_USER = "loggedin_user";
    public static final String SHARED_PREF_DATA = "shared_pref_data";

    // variables for user database
    public static final String USER_DATABASE_NAME = "user_db";
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_ID = "user_id";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHONE_NO = "phone_no";

    // variables for order database
    public static final String ORDER_DATABASE_NAME = "order_db";
    public static final String ORDER_TABLE_NAME = "orders";
    public static final String ORDER_ID = "order_id";
    public static final String GOOD_IMAGE = "good_image";
    public static final String RECEIVER_NAME = "receiver";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String LOCATION = "location";
    public static final String DESTINATION = "destination";
    public static final String GOOD_TYPE = "good_type";
    public static final String VEHICLE_TYPE= "vehicle_type";
    public static final String WEIGHT= "weight";
    public static final String WIDTH= "width";
    public static final String LENGTH= "length";
    public static final String HEIGHT= "height";

    // variables for truck database
    public static final String TRUCK_DATABASE_NAME = "truck_db";
    public static final String TRUCK_TABLE_NAME = "trucks";
    public static final String TRUCK_ID = "truck_id";
    public static final String TRUCK_IMAGE = "truck_image";
    public static final String TRUCK_NAME = "truck_name";
    public static final String TRUCK_DESCRIPTION = "truck_description";

    // function to create a toast
    public static void createToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // convert from Bitmap to byte array
    public static byte[] getBytesArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to Bitmap
    public static Bitmap getBitmapFromBytesArray(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // convert from Bitmap to Drawable
    public static Bitmap getBitmapFromDrawable(Context context, int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        return bitmap;
    }

 @SuppressLint("MissingPermission")
 public  static  boolean connectionAvailable(Context context){
  ConnectivityManager connectivityManager  = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

  if(connectivityManager !=null && connectivityManager.getActiveNetwork() !=null)
  {
   return  connectivityManager.getActiveNetworkInfo().isAvailable();
  }
  else {
   return  false;
  }
 }
}
