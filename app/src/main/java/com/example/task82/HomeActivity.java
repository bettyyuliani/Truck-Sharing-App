package com.example.task82;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task82.R;
import com.example.task82.util.Util;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    // variables for recycler view
    RecyclerView recyclerView;
    TrucksRecyclerViewAdapter recyclerViewAdapter;

    // variables for resources
    TypedArray truckImages, truckNames, truckDescriptions;

    // variable for database
    TruckDatabaseHelper truckDatabaseHelper;
    OrderDatabaseHelper orderDatabaseHelper = new OrderDatabaseHelper(this);

    String loggedInUsername;

    // items to load into recycler view
    ArrayList<Truck> trucks = new ArrayList<>();

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
                return true;
            case R.id.accountMenu:
                Intent accountIntent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(accountIntent);
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // create database helper object
        truckDatabaseHelper = new TruckDatabaseHelper(this);

        // get the logged in user's username from shared preferences and assign it to the respective variable
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");

        // load in trucks data to database
        if (truckDatabaseHelper.fetchAllTrucks().size() == 0)
        {
            createTruckDatabase();
        }


        // obtain all trucks data from trucks database
        trucks = truckDatabaseHelper.fetchAllTrucks();

        // obtain view
        recyclerView = findViewById(R.id.trucksRecyclerView);

        //create and set a new adapter to link recycler view to the associated data
        recyclerViewAdapter = new TrucksRecyclerViewAdapter(trucks,  this);
        recyclerView.setAdapter(recyclerViewAdapter);

//        create and set a layout manager to manage the layout of the view
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    // on click listener for add delivery
    public void addDeliveryClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
        startActivity(intent);
    }

    public void createTruckDatabase()
    {
        // obtain resources of truck data
        truckImages = getResources().obtainTypedArray(R.array.truckImage);
        truckNames = getResources().obtainTypedArray(R.array.truckName);
        truckDescriptions = getResources().obtainTypedArray(R.array.truckDescription);

        for (int i = 0; i < truckImages.length(); i++)
        {
            // resource of each truck data
            int imageResource = truckImages.getResourceId(i, -1);
            String nameResource = truckNames.getString(i);
            String descriptionResource = truckDescriptions.getString(i);

            // create new truck object
            Truck truck = new Truck(imageResource, nameResource, descriptionResource);

            // create new row in the database
            truckDatabaseHelper.insertTruck(truck);
        }
    }
}