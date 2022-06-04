package com.example.task82;


import android.content.Intent;
import android.graphics.Color;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);

        // iniatialises prompt text and login button
        TextView messageTextView = findViewById(R.id.messageTextView);
        final Button biometricLoginButton = findViewById(R.id.biometricLoginButton);

        // creates a BioMetric Manager object
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) // check if user has access to biometric sensor
        {

            // if user has access to biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
                messageTextView.setText("Login with Fingerprint");
                messageTextView.setTextColor(Color.parseColor("#fafafa"));
                break;

            // if device has no biometric sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                messageTextView.setText("Fingerprint sensor not found within this device");
                biometricLoginButton.setVisibility(View.GONE);
                break;

            // if biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                messageTextView.setText("The biometric sensor is unavailable");
                biometricLoginButton.setVisibility(View.GONE);
                break;

            // if no fingerprint record is found on the device
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                messageTextView.setText("There is no fingerprint recorded in this device");
                biometricLoginButton.setVisibility(View.GONE);
                break;
        }
        // creates an Executor object
        Executor executor = ContextCompat.getMainExecutor(this);
        // retrieve the authentication result
        final BiometricPrompt biometricPrompt = new BiometricPrompt(BiometricActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // if authentication is successful
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                biometricLoginButton.setText("Login Successful");

                // start home activity
                Intent homeIntent = new Intent(BiometricActivity.this, HomeActivity.class);
                startActivity(homeIntent);

            }

            // if authentication is not successful
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        // creates a prompt message
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Truck Sharing App")
                .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
        biometricLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);

            }
        });
    }
}