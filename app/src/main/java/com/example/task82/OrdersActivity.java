package com.example.task82;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task82.R;
import com.example.task82.util.Util;

import java.util.ArrayList;


public class OrdersActivity extends AppCompatActivity implements ItemClickListener {

    // variables for recycler view
    RecyclerView recyclerView;
    OrdersRecyclerViewAdapter ordersRecyclerViewAdapter;

    // database variable
    OrderDatabaseHelper db;

    // list of orders
    ArrayList<Order> orders = new ArrayList<>();

    // variable for the current logged in user
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // create a new database object
        db = new OrderDatabaseHelper(this);

        // get and set the current logged in user from the shared preferences
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        username = prefs.getString(Util.LOGGEDIN_USER, "");
        orders = db.fetchAllOrders(username);

        // obtain views
        recyclerView = findViewById(R.id.trucksRecyclerView);

        //create and set a new adapter to link recycler view to the associated data
        ordersRecyclerViewAdapter = new OrdersRecyclerViewAdapter(orders,  this);
        recyclerView.setAdapter(ordersRecyclerViewAdapter);

        // set on click listeners for the adapter
        // listener for item view
        ordersRecyclerViewAdapter.setClickListener(this);
        // listener for share button
        ordersRecyclerViewAdapter.shareClickListener(this);

        //create and set a layout manager to manage the layout of the view
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    // create menu on tool bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.truck_menu, menu);
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
                finish();
                return true;
            case R.id.accountMenu:
                Intent accountIntent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(accountIntent);
                finish();
                return true;
            case R.id.myordersMenu:
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

    // on click listener for each item
    @Override
    public void onClick(View view, int position) {
        final Order order = orders.get(position);
        createFragment(order);
    }
        // function for fragment creation
    public void createFragment(Order order)
    {
        Fragment fragment = new OrderFragment(order);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }


    // on click listener for each share image
    public void onShareClick(View view, int position) {
        Order order = orders.get(position);
        // start a new service
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String msg = "I am sending " + order.getReceiverName() + " a good on " + order.getDate() + " at " + order.getTime();
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


}