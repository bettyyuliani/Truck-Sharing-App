package com.example.task101;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

            // open home activity
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
        else {
            Toast.makeText(MainActivity.this, "The user does not exist", Toast.LENGTH_SHORT).show();
        }
    }
}