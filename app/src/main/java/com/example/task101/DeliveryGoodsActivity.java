package com.example.task101;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class DeliveryGoodsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // data variables
    String selectedGood, selectedVehicle, username;
    String length, width, height, weight;
    Order order;

    // database variable
    OrderDatabaseHelper databaseHelper;

    // view variables
    Spinner goodTypeSpinner;
    Spinner vehicleTypeSpinner;
    EditText lengthET, widthET, heightET, weightET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_goods);

        // create new database object
        databaseHelper = new OrderDatabaseHelper(this);

        // get views
        goodTypeSpinner = findViewById(R.id.goodTypeSpinner);
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        lengthET = findViewById(R.id.lengthEditText);
        widthET = findViewById(R.id.widthEditText);
        weightET = findViewById(R.id.weightEditText);
        heightET = findViewById(R.id.heightEditText);

        /// ==== adapter for good type ====
        // adapter for string array
        ArrayAdapter<CharSequence> goodTypeAdapter = ArrayAdapter.createFromResource(this, R.array.good_types, android.R.layout.simple_spinner_item);
        // layout for list of choices
        goodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // applying adapter to spinner
        goodTypeSpinner.setAdapter(goodTypeAdapter);
        goodTypeSpinner.setOnItemSelectedListener(this);

        // ==== adapter for vehicle type ====
        //adapter for string array
        ArrayAdapter<CharSequence> vehicleTypeAdapter = ArrayAdapter.createFromResource(this, R.array.vehicle_types, android.R.layout.simple_spinner_item);
        // // layout for list of choices
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // applying adapter to spinner
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
        vehicleTypeSpinner.setOnItemSelectedListener(this);
    }

    // on click listener for create order
    public void createOrderClick(View view)
    {
        if (selectedGood == "" || selectedVehicle == "")
        {
            Util.createToast(getApplicationContext(), "Please select good type and vehicle type");
        }
        else
        {
            // get details from previous activity
            Intent intent = getIntent();
            String receiverName = intent.getStringExtra(Util.RECEIVER_NAME);
            String location = intent.getStringExtra(Util.LOCATION);
            double locationLatitude = intent.getDoubleExtra(Util.LOCATION_LATITUDE, 0);
            double locationLongitude = intent.getDoubleExtra(Util.LOCATION_LONGITUDE, 0);
            String destination = intent.getStringExtra(Util.DESTINATION);
            double destinationLatitude = intent.getDoubleExtra(Util.DESTINATION_LATITUDE, 0);
            double destinationLongitude = intent.getDoubleExtra(Util.DESTINATION_LONGITUDE, 0);
            String date = intent.getStringExtra(Util.DATE);
            String time = intent.getStringExtra(Util.TIME);
            byte[] goodImageByte = intent.getByteArrayExtra(Util.GOOD_IMAGE);

            // get user input from this activity

            width = widthET.getText().toString();
            weight = weightET.getText().toString();
            height = heightET.getText().toString();
            length = lengthET.getText().toString();

            // get the current logged in user from shared preference
            SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
            username = prefs.getString(Util.LOGGEDIN_USER, "");

            // create new order
            order = new Order(username, goodImageByte, receiverName, date, time, location, locationLatitude, locationLongitude, destination, destinationLatitude, destinationLongitude, selectedGood, selectedVehicle, weight, width, length, height);

            // create a new row in the database by inserting the order
            long rowID = databaseHelper.insertOrder(order);

            if (rowID > 0) {
                Util.createToast(getApplicationContext(), "Order Successfully Created");
                Intent homeIntent = new Intent(this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
            else {
                Util.createToast(getApplicationContext(), "Error in Creating Order!");
            }
        }
    }

    // methods for AdapterView.OnItemSelectedListener
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        // if user chose something from good type spinner
        if (adapterView.getId() == R.id.goodTypeSpinner)
        {
            selectedGood = adapterView.getItemAtPosition(i).toString();
        }
        // if user chose something from vehicle type spinner
        else
        {
            selectedVehicle = adapterView.getItemAtPosition(i).toString();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}