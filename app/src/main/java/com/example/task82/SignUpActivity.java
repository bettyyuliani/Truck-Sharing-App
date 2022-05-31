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

    UserDatabaseHelper databaseHelper;
    ImageView addImageView;
    EditText signupFullNameEditText;
    EditText signupDateofBirthEditText;
    EditText signupAddressEditText;
    EditText signupUserNameEditText;
    EditText signupPasswordEditText;
    EditText signupConfirmPasswordEditText;
    EditText signupPhoneNoEditText;
    Button createAccountButton;

    public static final int PROFILE_PICTURE_REQUEST = 100;
    public static final int AUTOFILL_REQUEST = 200;
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

    //Check for results returned from the Gallery application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_PICTURE_REQUEST){

            if (resultCode == RESULT_OK)
            {
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
            else
            {
                setDefaultImage();
            }
        }
        else if (requestCode == AUTOFILL_REQUEST)
        {
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
                Task<FirebaseVisionText> firebaseVisionTextTask = textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                                    for (FirebaseVisionText.Line line: block.getLines()) {
                                        int lineCount = line.getElements().size();
                                        int index = 0;
                                        while (index < lineCount) {
                                            FirebaseVisionText.Element element = line.getElements().get(index);
                                            String text = element.getText().toLowerCase(Locale.ROOT);

                                            if (text.contains("name")) {
                                                for (int i = index + 1; i < lineCount - 1; i++) {
                                                    signupFullNameEditText.append(line.getElements().get(i).getText() + " ");
                                                }
                                                signupFullNameEditText.append(line.getElements().get(lineCount - 1).getText());
                                                break;
                                            } else if (text.contains("birth")) {
                                                for (int i = index + 1; i < lineCount - 1; i++) {
                                                    signupDateofBirthEditText.append(line.getElements().get(i).getText() + " ");
                                                }
                                                signupDateofBirthEditText.append(line.getElements().get(lineCount - 1).getText());
                                                break;
                                            } else if (text.contains("address")) {
                                                for (int i = index + 1; i < lineCount - 1; i++) {
                                                    signupAddressEditText.append(line.getElements().get(i).getText() + " ");
                                                }
                                                signupAddressEditText.append(line.getElements().get(lineCount - 1).getText());
                                                break;
                                            } else if (text.contains("phone")) {
                                                signupPhoneNoEditText.append(line.getElements().get(index+2).getText());
                                                break;
                                            } else {
                                                index++;
                                            }
                                        }
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Util.createToast(getApplicationContext(), "an error has occured!");
                                    }
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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