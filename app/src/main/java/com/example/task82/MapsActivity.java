package com.example.task82;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.task82.databinding.ActivityMapsBinding;
import com.example.task82.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final int REQUEST_PHONE_CALL = 200;
    private double destinationLatitude, destinationLongitude;
    private double locationLatitude, locationLongitude;
    private String location, destination;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String url;

    private DownloadTask downloadTask = new DownloadTask();

    TextView originTV, destinationTV, fareTV, timeTV;

    // declare variables used inside this activity
    private String receiverName;
    private String time;
    private String date;
    private String goodType;
    private String vehicleType;
    private String weight;
    private String width;
    private String length;
    private String height;
    byte[] goodImageBytesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get views
        originTV = findViewById(R.id.originMapTV);
        destinationTV = findViewById(R.id.destinationMapTV);
        fareTV = findViewById(R.id.fareMapTV);
        timeTV = findViewById(R.id.timeMapTV);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get data from order fragment
        Intent intent = getIntent();
        destinationLatitude = intent.getDoubleExtra(Util.DESTINATION_LATITUDE, 0);
        destinationLongitude = intent.getDoubleExtra(Util.DESTINATION_LONGITUDE, 0);
        locationLatitude = intent.getDoubleExtra(Util.LOCATION_LATITUDE, 0);
        locationLongitude = intent.getDoubleExtra(Util.LOCATION_LONGITUDE, 0);
        location = intent.getStringExtra(Util.LOCATION);
        destination = intent.getStringExtra(Util.DESTINATION);
        receiverName = intent.getStringExtra(Util.RECEIVER_NAME);
        time = intent.getStringExtra(Util.TIME);
        date = intent.getStringExtra(Util.DATE);
        goodType = intent.getStringExtra(Util.GOOD_TYPE);
        vehicleType = intent.getStringExtra(Util.VEHICLE_TYPE);
        weight = intent.getStringExtra(Util.WEIGHT);
        width = intent.getStringExtra(Util.WIDTH);
        length = intent.getStringExtra(Util.LENGTH);
        height = intent.getStringExtra(Util.HEIGHT);

        // set text on map activities
        originTV.setText(location);
        destinationTV.setText(destination);

        // get url for API
        url = getDirectionUrl(locationLatitude, locationLongitude, destinationLatitude, destinationLongitude);

        // create new json object request for duration
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray routesArray = response.getJSONArray("routes");
                    JSONArray legsArray = ((JSONObject)routesArray.get(0)).getJSONArray("legs");
                    JSONObject duration = legsArray.getJSONObject(0).getJSONObject("duration");

                    int fare = (int) duration.getDouble("value")/60*2;

                    timeTV.setText(duration.getString("text"));
                    fareTV.setText("$" + Integer.toString(fare));
                } catch (JSONException e) {
                    Log.i("masuk", e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "Error with API");
            }
        }
        );

        requestQueue.add(jsonObjectRequest);

        Log.i("tag", url);
        downloadTask.execute(url);
    }

    // download URL in background
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";


            try {
                data = downloadUrl(strings[0].toString()).toString();
            } catch (Exception e)
            {
                Log.i("Background task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    // get map path
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // create and draw map path
        @Override
        protected void onPostExecute(List<List<HashMap>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(16);
                lineOptions.color(Color.rgb(95,63,166));
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    // call API url
    private String downloadUrl(String url) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            java.net.URL strUrl = new URL(url);
            urlConnection = (HttpURLConnection) strUrl.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // get http url for API
    private String getDirectionUrl(double locationLatitude, double locationLongitude, double destinationLatitude, double destinationLongitude)
    {
        // origin of route
        String originLatLng = "?origin=" + locationLatitude + "," + locationLongitude;

        // destination of route
        String destinationLatLng = "destination=" + destinationLatitude + "," + destinationLongitude;

        // transportation mode
        String mode = "mode=driving";

        // building parameters to web service
        String parameters = originLatLng + "&" + destinationLatLng + "&" + mode;

        // output format
        String output = "json";

        // complete url
        return "https://maps.googleapis.com/maps/api/directions/" + output + parameters + "&key=" + Util.API_KEY;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in origin and move the camera
        LatLng origin = new LatLng(locationLatitude, locationLongitude);
        LatLng destination = new LatLng(destinationLatitude, destinationLongitude);
        mMap.addMarker(new MarkerOptions().position(origin).title("Origin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 13));
    }

    // on click listener for book
    public void bookClick(View view)
    {
        Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
        intent.putExtra(Util.LOCATION, location);
        intent.putExtra(Util.DESTINATION, destination);
        intent.putExtra(Util.RECEIVER_NAME, receiverName);
        intent.putExtra(Util.TIME, time);
        intent.putExtra(Util.DATE, date);
        intent.putExtra(Util.GOOD_TYPE, goodType);
        intent.putExtra(Util.VEHICLE_TYPE, vehicleType);
        intent.putExtra(Util.WEIGHT, weight);
        intent.putExtra(Util.WIDTH, width);
        intent.putExtra(Util.LENGTH, length);
        intent.putExtra(Util.HEIGHT, height);
        intent.putExtra(Util.GOOD_IMAGE, goodImageBytesArray);
        startActivity(intent);
    }

    // on click listener for call driver button
    public void callClick(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL); }
        else
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel: 048423233"));
            startActivity(intent);
        }
    }

    // on click listener for text driver button
    public void textClick (View view)
    {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

}