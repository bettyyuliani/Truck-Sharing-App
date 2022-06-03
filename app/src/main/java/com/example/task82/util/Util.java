package com.example.task82.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.task82.ChatMessagingService;
import com.example.task82.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Util {

    // firebase server key and sender id
    public static final String FIREBASE_KEY = "AAAA7HBdqgA:APA91bHyT6oL3rJ4iLRBSy9c9UVl019E8-Ww4zrFZhdzC5C-5SiMvxV_N_Flhb7GA7zSkWfPZpvUsdVCoDoImQB1u3qSV7yFIu4fJCXmNwUfChy8WRugCPeo_R7-75yI6JXZD_3K1Wn8";
    private static final String SENDER_ID = "1015497468416";

    // request code
    public static final int PICK_LOCATION_REQUEST = 200;
    public static final int PICK_DESTINATION_REQUEST = 300;

    // token nodes
    public static final String TOKENS = "tokens";
    public static final String DEVICE_TOKEN = "device_token";

    // chat nodes
    public static final String CHATS = "chats";
    public static final String USERS = "users";

    // messages nodes
    public static final String MESSAGE = "message";
    public static final String MESSAGE_ID = "message_ID";
    public static final String MESSAGE_TIME = "message_time";
    public static final String MESSAGE_SENDER = "message_sender";
    public static final String MESSAGES = "messages";

    // map api key
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
    public static final String VEHICLE_TYPE = "vehicle_type";
    public static final String WEIGHT = "weight";
    public static final String WIDTH = "width";
    public static final String LENGTH = "length";
    public static final String HEIGHT = "height";

    // variables for truck database
    public static final String TRUCK_DATABASE_NAME = "truck_db";
    public static final String TRUCK_TABLE_NAME = "trucks";
    public static final String TRUCK_ID = "truck_id";
    public static final String TRUCK_IMAGE = "truck_image";
    public static final String TRUCK_NAME = "truck_name";
    public static final String TRUCK_DESCRIPTION = "truck_description";

    // variables for notification
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_RECEIVER_TOKEN = "to";
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String NOTIFICATION_DATA = "data";
    public static final String CHANNEL_ID = "truck_sharing_app";
    public static final String CHANNEL_NAME = "truck_sharing_app_notifications";
    public static final String CHANNEL_DESCRIPTION = "Truck Sharing App Notifications";

    /**
    * function to create a toast
    * @param context activity for where this function is called
    * @param message message to be shown on toast
    */
    public static void createToast(Context context, String message) {
       Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


   /**
    * convert from Bitmap to byte array
    * @param bitmap bitmap to obtain bytes array from
    * @return bytes array from bitmap
    */
    public static byte[] getBytesArrayFromBitmap(Bitmap bitmap) {
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
       return stream.toByteArray();
    }

    /**
    * convert from byte array to Bitmap
    * @param image image byte array to decode bitmap from
    * @return bitmap from the input byte array
    */
    public static Bitmap getBitmapFromBytesArray(byte[] image) {
       return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

   /**
    * convert from drawable resource to bitmap
    * @param context activity for where this function is called
    * @param drawable drawable resource ID
    * @return bitmap decoded from drawable resource
    */
    public static Bitmap getBitmapFromDrawable(Context context, int drawable) {
       Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
       return bitmap;
    }

   /**
    * checks if connecgtion is available
    * @param context activity for where this function is called
    */
    @SuppressLint("MissingPermission")
    public static boolean connectionAvailable(Context context) {
       ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // create ConnectivityManager object

       // if network exists
       if (connectivityManager != null && connectivityManager.getActiveNetwork() != null) {
          return connectivityManager.getActiveNetworkInfo().isAvailable();
       }
       // if network does not exist
       else {
          return false;
       }
    }

   /**
    * update device token whenever it refreshes
    * @param context activity for where this function is called
    * @param token new token
    * @param username username whose token belongs to
    */
    public static void updateDeviceToken(final Context context, String token, String username) {

       DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(); // get root node of the firebase
       DatabaseReference databaseReference = rootRef.child(Util.TOKENS).child(username); // get token node associated to the user

       // create new hashmap, used to specify nodes in the firebase
       HashMap<String, String> hashMap = new HashMap<>();
       hashMap.put(Util.DEVICE_TOKEN, token);

       // set values of nodes in the firebase
       databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

          // on click listener for if the node update is completed
          @Override
          public void onComplete(@NonNull Task<Void> task) {
             if (!task.isSuccessful()) // if token is failed to be updated
             {
               Util.createToast(context, "Failed to update token");
             }
          }
       });
    }

   /**
    * function to send notification
    *
    * @param context activity for where this function is called
    * @param title title of notification
    * @param message body or message of notification
    * @param receiverUsername username of the notification receiver
    */
    public  static  void sendNotification(final Context context, final String title, final String message, String receiverUsername) {

     // get the root node in the firebase
     DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
     // get the token node of the associated username
     DatabaseReference databaseReference = rootRef.child(Util.TOKENS).child(receiverUsername);

     // listener for changes in the associated node
     databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

       String deviceToken = dataSnapshot.child(Util.DEVICE_TOKEN).getValue().toString(); // get value of device token
       JSONObject notification = new JSONObject(); // create JSON object for notification
       JSONObject notificationData = new JSONObject(); // create JSON object for data message (handled by client app)

       try {
           // put data associated to the data message into the JSON object
           notificationData.put(Util.NOTIFICATION_TITLE, title);
           notificationData.put(Util.NOTIFICATION_MESSAGE, message);

           // bind information associated to the notification
           notification.put(Util.NOTIFICATION_RECEIVER_TOKEN, deviceToken);
           notification.put(Util.NOTIFICATION_DATA, notificationData);

           // FCM API to send notification
            String url = "https://fcm.googleapis.com/fcm/send";

            // listener for successful response from the API
            Response.Listener successListener = new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    Util.createToast(context.getApplicationContext(), "Notification sent!");
                }
            };

            // listener for failed response from the API
            Response.ErrorListener failureListener = new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                    Util.createToast(context.getApplicationContext(), "Failed to send notification!" + error.toString());
                 }
            };

            // create a new JSON object request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, notification, successListener, failureListener) {

                // API header
                 @Override
                 public Map<String, String> getHeaders() throws AuthFailureError {

                     // provide authentication details
                     Map<String, String> params = new HashMap<>();
                     params.put("Authorization", "key=" + Util.FIREBASE_KEY);
                     params.put("Sender", "id=" + Util.SENDER_ID);
                     params.put("Content-Type", "application/json");

                     return params;
                 }

                 // getter for body content type as authentication details
                 @Override
                 public String getBodyContentType() {
                  return "application/json";
                 }
            };

            // make async call JSON object request
            RequestQueue requestQueue = Volley.newRequestQueue(context); // create request queue
            requestQueue.add(jsonObjectRequest); // add request into the queue, make

       }
       // catch exceptions if json call was failed
       catch (JSONException e) {
            Util.createToast(context.getApplicationContext(), "Failed to send notification! " + e.toString());
       }
      }

      // request cancellation listener due to database error
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
          Util.createToast(context, "failed to send notification" + databaseError.toString());
      }
     });
    }
}
