package com.example.task82;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task82.R;
import com.example.task82.util.Util;

import java.io.FileNotFoundException;
import java.io.InputStream;

//ABOUT: Activity to view user account details and/or update them
public class AccountActivity extends AppCompatActivity {

    // view variables
    ImageView accImageView;
    EditText accFullNameET, accUsernameET, accPasswordET, accConfirmPasswordET, accPhoneNoET;
    Button accCancelButton, accSaveButton;

    // data variables
    User user;
    byte[] userImageBytesArray;
    Bitmap userImageBitmap;
    String userFullName, username, password, passwordConfirm, phoneNumber;
    String loggedInUsername; // keep track of logged in username

    // request variable
    final int GALLERY_REQUEST = 100;

    // database variables
    UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(this);
    OrderDatabaseHelper orderDatabaseHelper = new OrderDatabaseHelper(this);

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // obtain views
        accImageView = findViewById(R.id.accImageView);
        accFullNameET = findViewById(R.id.accFullNameET);
        accUsernameET = findViewById(R.id.accUsernameET);
        accPasswordET = findViewById(R.id.accPasswordET);
        accConfirmPasswordET = findViewById(R.id.accConfirmPasswordET);
        accPhoneNoET = findViewById(R.id.accPhoneNoET);
        accCancelButton = findViewById(R.id.accCancelButton);
        accSaveButton = findViewById(R.id.accSaveButton);

        // get the logged in user's username from shared preferences and assign it to the respective variable
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");

        // get data about the logged in user
        user = userDatabaseHelper.getUser(loggedInUsername);

        // assign the image
        // this variable is useful for image prompt
        userImageBytesArray = user.getImage();
        accFullNameET.setHint("Full name - " + user.getName());
        accUsernameET.setHint("Username - " + user.getUsername());
        accPhoneNoET.setHint("Phone No - " + user.getPhoneNo());

        // display views
        try {
            accImageView.setImageBitmap(Util.getBitmapFromBytesArray(user.getImage()));

        } catch (Exception e) {
            Log.i("Error", e.toString());
        }
    }

    // create menu on tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.truck_menu, menu);

        if (orderDatabaseHelper.getOrderCount(loggedInUsername) != 0)
        {
            MenuItem textDriver = menu.findItem(R.id.textDriverMenu);
            textDriver.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    // on click listener for selected options from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.homeMenu:
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                return true;
            case R.id.accountMenu:
                return true;
            case R.id.myordersMenu:
                Intent ordersIntent = new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(ordersIntent);
                return true;
            case R.id.textDriverMenu:
                Intent messageIntent = new Intent(getApplicationContext(), MessageActivity.class);
                startActivity(messageIntent);
                return true;
            case R.id.logoutMenu:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // on click listener for opening up gallery
    public void accChangePicture(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    // results returned from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();  // image's address
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    userImageBitmap = BitmapFactory.decodeStream(inputStream);

                    userImageBytesArray = Util.getBytesArrayFromBitmap(userImageBitmap);

                    // assign bitmap to the image view
                    accImageView.setImageBitmap(userImageBitmap);
                } catch (FileNotFoundException e) {
                    Util.createToast(this, "File not found!");
                }
            }
        }
    }

    // on click listener for cancel changes
    public void cancelChangesClick(View view)
    {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    // on click listener for save changes
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void saveChangesClick(View view) {

        // get user input data
        userFullName = accFullNameET.getText().toString();
        username = accUsernameET.getText().toString();
        password = accPasswordET.getText().toString();
        passwordConfirm = accConfirmPasswordET.getText().toString();
        phoneNumber = accPhoneNoET.getText().toString();

        ContentValues contentValues = new ContentValues();

        // if user tries to change their username but it exists in the database
        if (!username.equals("") && !username.equals(user.getUsername()) && userDatabaseHelper.userExists(username))
        {
            Util.createToast(this, "Username unavailable");
        }
        // if user tries to change their password but it is not the same
        else if (!password.equals(passwordConfirm))
        {
            Util.createToast(this, "Password mismatch");
        }
        else
        {
            // if user made changes to profile picture
            if (!userImageBytesArray.equals(user.getImage()))
            {
                contentValues.put(Util.PROFILE_PICTURE, userImageBytesArray);
                user.setImage(userImageBytesArray);
            }
            // if user made changes to full name
            if (!userFullName.equals("") && !userFullName.equals(user.getName()))
            {
                contentValues.put(Util.NAME, userFullName);
                user.setName(userFullName);
            }
            // if user made changes to username
            if (!username.equals("") && !username.equals(user.getUsername()))
            {
                contentValues.put(Util.USERNAME, username);
                user.setUsername(username);
            }
            // if user made changes to password
            if (!password.equals("") && !password.equals(user.getPassword()))
            {
                contentValues.put(Util.PASSWORD, password);
                user.setPassword(password);
            }
            // if user made changes to phone number
            if (!phoneNumber.equals("") && !phoneNumber.equals(user.getPhoneNo()))
            {
                contentValues.put(Util.PHONE_NO, phoneNumber);
                user.setPhoneNo(phoneNumber);
            }
            // if user made changes
            if (!contentValues.isEmpty())
            {
                // update data in the database
                int res = userDatabaseHelper.updateDetails(loggedInUsername, contentValues);
                if (res > 0) {
                    if (!loggedInUsername.equals(user.getUsername()))
                    {
                        // update orders in the order table
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Util.USERNAME, user.getUsername());
                        OrderDatabaseHelper order_db = new OrderDatabaseHelper(this);
                        order_db.updateUsername(loggedInUsername, contentValues1);

                        // update logged in username in the shared preferences
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Util.LOGGEDIN_USER, user.getUsername());
                        editor.apply();

                        // reassign the username for logged in user
                        loggedInUsername = sharedPreferences.getString(Util.LOGGEDIN_USER, "");
                    }

                    Util.createToast(this, "Changes saved successfully");
                }
                else {
                    Util.createToast(this, "Changes not saved");
                }
            }
            // if user did not make any changes
            else
            {
                Util.createToast(this, "No changes made");
            }
            // go back to home page
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        }
    }
}