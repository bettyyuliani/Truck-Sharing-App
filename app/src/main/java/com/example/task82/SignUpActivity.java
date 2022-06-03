package com.example.task82;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task82.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;


public class SignUpActivity extends AppCompatActivity {

    // database variable
    UserDatabaseHelper databaseHelper;

    // view variables
    ImageView addImageView;
    EditText signupFullNameEditText;
    EditText signupDateofBirthEditText;
    EditText signupAddressEditText;
    EditText signupUserNameEditText;
    EditText signupPasswordEditText;
    EditText signupConfirmPasswordEditText;
    EditText signupPhoneNoEditText;
    Button createAccountButton;

    // variables for image
    private Bitmap image;
    byte[] userImageBytes;

    // request variables
    public static final int PROFILE_PICTURE_REQUEST = 100;
    public static final int AUTOFILL_REQUEST = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        // obtain views
        addImageView = findViewById(R.id.addImageView);
        signupFullNameEditText = findViewById(R.id.signupFullNameEditText);
        signupDateofBirthEditText = findViewById(R.id.signupDateofBirthEditText);
        signupAddressEditText = findViewById(R.id.signupAddressEditText);
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
        startActivityForResult(intent, PROFILE_PICTURE_REQUEST);
    }

    // check for results returned from intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if result is obtained from profile picture request
        if (requestCode == PROFILE_PICTURE_REQUEST){

            // if RESULT_OK is received from the intent
            if (resultCode == RESULT_OK)
            {
                Uri imageUri = data.getData();  // get uri of selected image
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageUri); // get the InputStream from the uri
                    image = BitmapFactory.decodeStream(inputStream); // decode the InputStream into bitmap

                    userImageBytes = Util.getBytesArrayFromBitmap(image); // get bytes array of the selected image to be stored into the database

                    addImageView.setImageBitmap(image); // set image view to selected image
                }
                // catch file not found exception
                catch (FileNotFoundException e) {
                    Util.createToast(this, "File not found!");
                }
            }
            else
            {
                setDefaultImage(); // if no image was selected, set to default image
            }
        }

        // if result is obtained from autofill request
        if (requestCode == AUTOFILL_REQUEST)
        {
            // create FirebaseVisionImage object
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData()); // parse image to FirebaseVisionImage
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer(); // create TextRecognizer object

                // start to process text recognition from the selected image
                Task<FirebaseVisionText> firebaseVisionTextTask = textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    // if process is successful
                    @Override
                    public void onSuccess(FirebaseVisionText result) {

                        // traverse all text blocks in the image
                        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {

                            // traverse all lines in the block
                            for (FirebaseVisionText.Line line: block.getLines()) {

                                int lineCount = line.getElements().size(); // count of line
                                    int index = 0; // starting index

                                        // traverse each element in the line
                                        while (index < lineCount) {

                                            // obtain text in the line
                                            FirebaseVisionText.Element element = line.getElements().get(index);
                                            String text = element.getText().toLowerCase(Locale.ROOT);

                                            // if line is associated to the name of the user
                                            if (text.contains("name")) {
                                                // obtain name
                                                for (int i = index + 1; i < lineCount - 1; i++) {
                                                    signupFullNameEditText.append(line.getElements().get(i).getText() + " ");
                                                }
                                                signupFullNameEditText.append(line.getElements().get(lineCount - 1).getText());
                                                break;
                                            }

                                            // if line is associated to the date of birth of the user
                                            else if (text.contains("birth")) {
                                                // obtain date of birth
                                                for (int i = index + 1; i < lineCount - 1; i++) {
                                                    signupDateofBirthEditText.append(line.getElements().get(i).getText() + " ");
                                                }
                                                signupDateofBirthEditText.append(line.getElements().get(lineCount - 1).getText());
                                                break;
                                            }

                                            // if line is associated to the address of the user
                                            else if (text.contains("address")) {
                                                // obtain address
                                                for (int i = index + 1; i < lineCount - 1; i++) {
                                                    signupAddressEditText.append(line.getElements().get(i).getText() + " ");
                                                }
                                                signupAddressEditText.append(line.getElements().get(lineCount - 1).getText());
                                                break;
                                            }

                                            // if line is associated to the phone number of the user
                                            else if (text.contains("phone")) {
                                                signupPhoneNoEditText.append(line.getElements().get(index+2).getText());
                                                break;
                                            }

                                            // if line is not associated to any information
                                            else {
                                                index++;
                                            }
                                        }
                                    }
                                }
                            }
                        })
                        // listener for if the text recognition process failed
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Util.createToast(getApplicationContext(), "an error has occured!");
                            }
                        });
            }
            // catch exceptions
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // on click listener for autofill request
    public void autofillClick(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), AUTOFILL_REQUEST);
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
        // obtain data from edit text
        String fullName = signupFullNameEditText.getText().toString();
        String userName = signupUserNameEditText.getText().toString();
        String password = signupPasswordEditText.getText().toString();
        String confirmPassword = signupConfirmPasswordEditText.getText().toString();
        String phoneNo = signupPhoneNoEditText.getText().toString();

        // checks if username exists
        if (databaseHelper.userExists(userName))
        {
            Util.createToast(SignUpActivity.this, "Username unavailable!");
        }
        else
        {
            // checks if two password matches
            if (password.equals(confirmPassword)) {

                if (userImageBytes == null)
                {
                    setDefaultImage(); // set to default profile picture if no picture was selected
                }

                // create a new entry in database
                long rowID = databaseHelper.insertUser(new User(userImageBytes, fullName, userName, password, phoneNo));

                // if entry was created successfully
                if (rowID > 0) {
                    Util.createToast(SignUpActivity.this, "Registered successfully!");

                    // go back to login page
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                // if entry creation was not successful
                else {
                    Util.createToast(SignUpActivity.this, "Registration error!");
                }
            }
            // if two passwords do not match
            else {
                Util.createToast(SignUpActivity.this, "Passwords do not match!");
            }
        }
    }
}