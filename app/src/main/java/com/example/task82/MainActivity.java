package com.example.task82;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.task82.R;
import com.example.task82.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // database variable
    UserDatabaseHelper databaseHelper;

    // view variables
    EditText loginusernameEditText;
    EditText loginpasswordEditText;

    // keeping tack of the username of logged in user
    String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create database object
        databaseHelper = new UserDatabaseHelper(this);

        // get view
        loginusernameEditText = (EditText) findViewById(R.id.loginusernameEditText);
        loginpasswordEditText = (EditText) findViewById(R.id.loginpasswordEditText);

    }

    // on click listener for sign up command
    public void signupClick(View view)
    {
        Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    // on click listener for log in command
    public void loginClick(View view)
    {
//        Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
//        startActivity(intent);
        String username = loginusernameEditText.getText().toString();
        String password = loginpasswordEditText.getText().toString();
        boolean result = databaseHelper.fetchUser(username, password);

        if (result == true) {

            loggedInUser = username;

            // push logged in username to shared preferences
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE); // access hard drive through shared preferences object
            SharedPreferences.Editor editor = sharedPreferences.edit(); // variable to let shared preferences editable
            editor.putString(Util.LOGGEDIN_USER, loggedInUser); // key value pair for username
            editor.apply(); // apply changes

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("error", "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();

                            Log.i("token", token);
                            Util.updateDeviceToken(getApplicationContext(), token, loggedInUser);
                        }
                    });


            if (loggedInUser.equals("driver"))
            {
                Intent intent = new Intent(this, DriverChatsActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                // open home activity
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }
        else {
            Toast.makeText(MainActivity.this, "The user does not exist", Toast.LENGTH_SHORT).show();
        }
    }
    public void loginWithFingerprintClick(View view) {
        // open biometric activity
        Intent homeIntent = new Intent(MainActivity.this, BiometricActivity.class);
        startActivity(homeIntent);
    }
    }