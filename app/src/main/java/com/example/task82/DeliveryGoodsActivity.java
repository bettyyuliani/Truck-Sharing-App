package com.example.task82;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task82.R;
import com.example.task82.ml.Model;
import com.example.task82.util.Util;

import org.checkerframework.checker.units.qual.A;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class DeliveryGoodsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // data variables
    String selectedGood, selectedVehicle, username;
    String length, width, height, weight;
    Order order;
    String goodTypeML;
    double goodTypeAccuracyML;

    // database variable
    OrderDatabaseHelper databaseHelper;

    // view variables
    Spinner goodTypeSpinner;
    Spinner vehicleTypeSpinner;
    EditText lengthET, widthET, heightET, weightET;
    ImageView goodImageView;
    TextView accuracyTextView, accuracyNumTextView;

    // variables for image and gallery service
    public static final int IMAGE_GALLERY_REQUEST = 100;
    private Bitmap image;
    byte[] goodImageBytes;

    // adapter for spinners
    ArrayAdapter<CharSequence> goodTypeAdapter;
    ArrayAdapter<CharSequence> vehicleTypeAdapter;

    // variables for good type classification with machine learning
    int imageSize = 224;
    String[] classes = {"Chair", "Bed", "Table", "Fridge", "Computer", "Watch", "Dish", "Bread", "Pizza",
            "Chips", "Vegetables", "Rice", "Nuts", "Metal", "Timber", "Coal", "Dirt"};

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
        accuracyNumTextView = findViewById(R.id.accuracyNumTV);
        accuracyTextView = findViewById(R.id.accuracyTV);
        goodImageView = findViewById(R.id.goodImage);

        /// ==== adapter for good type ====
        goodTypeAdapter = ArrayAdapter.createFromResource(this, R.array.good_types, android.R.layout.simple_spinner_item); // adapter for string array
        goodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout for list of choices
        goodTypeSpinner.setAdapter(goodTypeAdapter); // applying adapter to spinner
        goodTypeSpinner.setOnItemSelectedListener(this); // user selection listener

        // ==== adapter for vehicle type ====
        vehicleTypeAdapter = ArrayAdapter.createFromResource(this, R.array.vehicle_types, android.R.layout.simple_spinner_item); //adapter for string array
        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout for list of choices
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter); // applying adapter to spinner
        vehicleTypeSpinner.setOnItemSelectedListener(this); // user selection listener
    }

    // on click listener for create order
    public void createOrderClick(View view)
    {
        // set to default image if user didn't choose any image
        if (goodImageBytes == null)
        {
            setDefaultImage();
        }

        // if no good or vehicle type was selected
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

            // get user input from this activity
            width = widthET.getText().toString();
            weight = weightET.getText().toString();
            height = heightET.getText().toString();
            length = lengthET.getText().toString();

            // get the current logged in user from shared preference
            SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
            username = prefs.getString(Util.LOGGEDIN_USER, "");

            // create new order
            order = new Order(username, goodImageBytes, receiverName, date, time, location, locationLatitude, locationLongitude, destination, destinationLatitude, destinationLongitude, selectedGood, selectedVehicle, weight, width, length, height);

            // create a new row in the database by inserting the order
            long rowID = databaseHelper.insertOrder(order);

            if (rowID > 0) {
                Util.createToast(getApplicationContext(), "Order Successfully Created");
                Intent orderIntent = new Intent(this, OrdersActivity.class);
                startActivity(orderIntent);
                finish();
            }
            else {
                Util.createToast(getApplicationContext(), "Error in Creating Order!");
            }
        }
    }

    // if user doesn't select a photo, set a default image for the user
    public void setDefaultImage()
    {
        Bitmap defaultPic = BitmapFactory.decodeResource(getResources(), R.drawable.box);
        goodImageBytes = Util.getBytesArrayFromBitmap(defaultPic);
        goodImageView.setImageBitmap(defaultPic);
    }

    // listener to add photo
    public void popPhotoPicker(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
    }

    // check results returned from gallery
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if request code is a gallery request
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            // if RESULT_OK code is received
            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData(); // get uri of the image
                InputStream inputStream;

                try {

                    inputStream = getContentResolver().openInputStream(imageUri); // get InputStream from the image uri
                    image = BitmapFactory.decodeStream(inputStream); // get the bitmap data of the image
                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false); // scale image bitmap to intended size
                    goodImageBytes = Util.getBytesArrayFromBitmap(image);
                    goodImageView.setImageBitmap(image); // set image view
                    classifyImage(image); // classify good type with machine learning

                }
                // if no file was found
                catch (FileNotFoundException e) {
                    Util.createToast(this, "File not found!");
                }
            }
            // if no image was selected
            else {
                setDefaultImage(); // set to default image
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
        // TODO override method if no selection was made on the spinner
    }

    // classify the good type of the selected image with machine learning
    // reference: https://www.youtube.com/watch?v=jhGm4KDafKU&t=616s
    @SuppressLint("SetTextI18n")
    public void classifyImage(Bitmap image) {
        try {
            // creates the machine learning model
            Model model = Model.newInstance(getApplicationContext());

            // creates input for reference
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get pixel values of the image
            int [] intValues = new int[imageSize * imageSize]; //224 * 224
            image.getPixels(intValues, 0, image.getWidth(), 0 , 0, image.getWidth(), image.getHeight());

            int pixel = 0;

            // pixels of image in horizontal axis
            for (int i = 0; i < imageSize; i++) {
                // pixels of image in vertial axis
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    // extract RGB of each pixel
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 0xFF) * (1.f / 255.f)));
                }
            }

            inputFeature0.loadBuffer(byteBuffer); // load byte buffer

            // allows model to process input
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // variables for the confidence
            float[] accuracy  = outputFeature0.getFloatArray();
            int maxPos = 0; // position of maximum accuracy
            float maxAccuracy = 0; // initialises maximum accuracy

            // find the maximum accuracy out of the results
            for (int i = 0; i < accuracy.length; i++) {
                if (accuracy[i] > maxAccuracy) {
                    maxAccuracy = accuracy[i];
                    maxPos = i;
                }
            }

            goodTypeML = classes[maxPos]; // good type classified
            goodTypeAccuracyML = maxAccuracy; // accuracy of classification

            // set views according to the classification result
            goodTypeSpinner.setSelection(goodTypeAdapter.getPosition(goodTypeML)); // set spinner selection
            accuracyNumTextView.setText(String.format("%.2f%%", goodTypeAccuracyML*100)); // display accuracy of the calculation

            // unhide views
            accuracyTextView.setVisibility(View.VISIBLE);
            accuracyNumTextView.setVisibility(View.VISIBLE);
            // release model resource
            model.close();
        }
        catch (IOException e) {
            // TODO Handle exception
        }
    }
}