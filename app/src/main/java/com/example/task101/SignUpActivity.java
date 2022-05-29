package com.example.task101;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task101.util.Util;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class SignUpActivity extends AppCompatActivity {

    UserDatabaseHelper databaseHelper;
    ImageView addImageView;
    EditText signupFullNameEditText;
    EditText signupUserNameEditText;
    EditText signupPasswordEditText;
    EditText signupConfirmPasswordEditText;
    EditText signupPhoneNoEditText;
    Button createAccountButton;

    public static final int IMAGE_GALLERY_REQUEST = 100;
    private Bitmap image;
    byte[] userImageBytes;
    //Request variable


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        // obtain views
        addImageView = findViewById(R.id.addImageView);
        signupFullNameEditText = findViewById(R.id.signupFullNameEditText);
        signupUserNameEditText = findViewById(R.id.signupUsernameEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        signupConfirmPasswordEditText = findViewById(R.id.signupConfirmPasswordEditText);
        signupPhoneNoEditText = findViewById(R.id.signupPhoneNoEditText);
        createAccountButton = findViewById(R.id.createAccountButton);

        // create a new database helper object
        databaseHelper = new UserDatabaseHelper(this);
    }

    // on click listener for adding a new image, opens up the gallery service
    public void addDisplayPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
    }

    //Check for results returned from the Gallery application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST){
                Uri imageUri = data.getData();  // The address of the image
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    image = BitmapFactory.decodeStream(inputStream);

                    userImageBytes = Util.getBytesArrayFromBitmap(image);

                    addImageView.setImageBitmap(image);
                }
                catch (FileNotFoundException e) {
                    Util.createToast(this, "File not found!");
                }
            }
        }
        else
        {
            setDefaultImage();
        }
    }

    // set a default image if user doesn't choose an image
    public void setDefaultImage()
    {
        Bitmap defaultPic = BitmapFactory.decodeResource(getResources(), R.drawable.head);
        userImageBytes = Util.getBytesArrayFromBitmap(defaultPic);
        addImageView.setImageBitmap(defaultPic);
    }

    // on click listener for create account (finalizing button)
    public void createAccountClick(View view)
    {
        String fullName = signupFullNameEditText.getText().toString();
        String userName = signupUserNameEditText.getText().toString();
        String password = signupPasswordEditText.getText().toString();
        String confirmPassword = signupConfirmPasswordEditText.getText().toString();
        String phoneNo = signupPhoneNoEditText.getText().toString();

        if (databaseHelper.userExists(userName))
        {
            Util.createToast(SignUpActivity.this, "Username unavailable!");
        }
        else
        {
            if (password.equals(confirmPassword)) {
                if (userImageBytes == null)
                {
                    setDefaultImage();
                }
                long rowID = databaseHelper.insertUser(new User(userImageBytes, fullName, userName, password, phoneNo));

                if (rowID > 0) {
                    Util.createToast(SignUpActivity.this, "Registered successfully!");
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Util.createToast(SignUpActivity.this, "Registration error!");
                }
            }
            else {
                Util.createToast(SignUpActivity.this, "Passwords do not match!");
            }
        }
    }
}