package com.example.task101;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class PlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        // initialize the SDK
        Places.initialize(getApplicationContext(), Util.API_KEY);

        // get the auto-complete fragment
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // set fields of the location data
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // set on click listener for a chosen place
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Util.createToast(getApplicationContext(), "Error with API Key");
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                // get the details of the selected place
                LatLng latLng = place.getLatLng();
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                String placeName = place.getName();


                // check if the place is pick up location/destination
                Intent intent1 = getIntent();
                int requestCode = intent1.getIntExtra("requestCode", 0);

                // pass the details back to delivery activity
                Intent intent2 = new Intent(getApplicationContext(), DeliveryActivity.class);

                if (requestCode == Util.PICK_LOCATION_REQUEST)
                {
                    intent2.putExtra(Util.LOCATION_LATITUDE, latitude);
                    intent2.putExtra(Util.LOCATION_LONGITUDE, longitude);
                    intent2.putExtra(Util.LOCATION, placeName);
                    setResult(RESULT_OK, intent2);
                    finish();
                }
                else if (requestCode == Util.PICK_DESTINATION_REQUEST)
                {
                    intent2.putExtra(Util.DESTINATION_LATITUDE, latitude);
                    intent2.putExtra(Util.DESTINATION_LONGITUDE, longitude);
                    intent2.putExtra(Util.DESTINATION, placeName);
                    setResult(RESULT_OK, intent2);
                    finish();
                }
                else
                {
                    setResult(RESULT_CANCELED, intent2);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}