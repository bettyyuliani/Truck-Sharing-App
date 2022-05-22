package com.example.task101;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrderFragment extends Fragment {

    // declare variables used inside this fragment
    private String receiverName;
    private String time;
    private String date;
    private String location;
    private String destination;
    private String goodType;
    private String vehicleType;
    private String weight;
    private String width;
    private String length;
    private String height;
    Bitmap goodImageBitmap;
    byte[] goodImageBytesArray;

    private double locationLatitude, locationLongitude, destinationLatitude, destinationLongitude;

    // fragment constructor
    public OrderFragment(Order order) {

        this.receiverName = order.getReceiverName();
        this.time = order.getTime();
        this.date = order.getDate();
        this.location = order.getLocation();
        this.locationLatitude = order.getLocationLatitude();
        this.locationLongitude = order.getLocationLongitude();
        this.destination = order.getDestination();
        this.destinationLongitude = order.getDestinationLongitude();
        this.destinationLatitude = order.getDestinationLatitude();
        this.goodType = order.getGoodType();
        this.vehicleType = order.getVehicleType();
        this.weight = order.getWeight();
        this.width = order.getWidth();
        this.length = order.getLength();
        this.height = order.getHeight();
        this.goodImageBytesArray = order.getGoodImageByte();
    }

    // non graphical initialisations
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodImageBitmap = Util.getBitmapFromBytesArray(goodImageBytesArray);
    }

    // Inflate the layout for this fragment and initialise graphical variables
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    // modifying UI elements
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // obtain views
        ImageView fragmentGoodImageView = view.findViewById(R.id.fragmentGoodImageView);
        TextView receiverNameTextView = view.findViewById(R.id.receiverFragmentTV);
        TextView dateTextView = view.findViewById(R.id.dateFragmentTV);
        TextView timeTextView = view.findViewById(R.id.timeFragmentTV);
        TextView locationTextView = view.findViewById(R.id.locationFragmentTV);
        TextView destinationTextView = view.findViewById(R.id.destinationFragmentTV);
        TextView goodTypeTextView = view.findViewById(R.id.goodTypeFragmentTV);
        TextView vehicleTypeTextView = view.findViewById(R.id.vehicleTypeTV);
        TextView weightTextView = view.findViewById(R.id.weightFragmentTV);
        TextView lengthTextView = view.findViewById(R.id.lengthFragmentTV);
        TextView heightTextView = view.findViewById(R.id.heightFragmentTV);
        TextView widthTextView = view.findViewById(R.id.widthFragmentTV);
        Button estimateButton = view.findViewById(R.id.getEstimateButton);

        estimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra(Util.LOCATION_LATITUDE, locationLatitude);
                intent.putExtra(Util.LOCATION_LONGITUDE, locationLongitude);
                intent.putExtra(Util.DESTINATION_LATITUDE, destinationLatitude);
                intent.putExtra(Util.DESTINATION_LONGITUDE, destinationLongitude);
                intent.putExtra(Util.LOCATION, location);
                intent.putExtra(Util.DESTINATION, destination);
                startActivity(intent);
            }
        });

        // set views according to its data
        fragmentGoodImageView.setImageBitmap(goodImageBitmap);
        receiverNameTextView.setText(receiverName);
        dateTextView.setText(date);
        timeTextView.setText(time);
        dateTextView.setText(date);
        locationTextView.setText(location);
        destinationTextView.setText(destination);
        goodTypeTextView.setText(goodType);
        vehicleTypeTextView.setText(vehicleType);
        weightTextView.setText(weight);
        lengthTextView.setText(length);
        heightTextView.setText(height);
        widthTextView.setText(width);
    }
}