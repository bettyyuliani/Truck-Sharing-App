package com.example.task82;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task82.R;
import com.example.task82.util.Util;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DeliveryActivity extends AppCompatActivity {

    // variables to construct time string
    int hour, minute;
    String format;

    // variables for location

    double locationLatitude, locationLongitude;
    double destinationLatitude, destinationLongitude;

    // variables for order details
    String receiverName, pickUpDate, pickUpTime, pickUpLocation, destination = null;

    // view variables
    CalendarView calendarView;
    EditText receiverNameEditText, pickUpLocationEditText, destinationEditText;
    TextView timeTextView;
    ImageView goodImageView;

    // variables for image and gallery service
    public static final int IMAGE_GALLERY_REQUEST = 100;
    private Bitmap image;
    byte[] goodImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        // get views
        goodImageView = findViewById(R.id.goodsImageView);
        receiverNameEditText = findViewById(R.id.receiverNameEditText);
        pickUpLocationEditText = findViewById(R.id.locationEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        timeTextView = findViewById(R.id.timeTextView);
        calendarView = findViewById(R.id.dateCalendarView);

        // set on click listener for calendar, used to obtain date selected
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
                pickUpDate = date + "/" + (month+1) + "/" + year;
            }
        });
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

        if (requestCode == IMAGE_GALLERY_REQUEST)
        {
            if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    InputStream inputStream;

                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);
                        image = BitmapFactory.decodeStream(inputStream);
                        goodImageBytes = Util.getBytesArrayFromBitmap(image);
                        goodImageView.setImageBitmap(image);
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
        else if (requestCode == Util.PICK_LOCATION_REQUEST && resultCode == RESULT_OK)
        {
            pickUpLocation = data.getStringExtra(Util.LOCATION);
            pickUpLocationEditText.setText(pickUpLocation);
            locationLatitude = data.getDoubleExtra(Util.LOCATION_LATITUDE, 0);
            locationLongitude = data.getDoubleExtra(Util.LOCATION_LONGITUDE, 0);
        }
        else if (requestCode == Util.PICK_DESTINATION_REQUEST && resultCode == RESULT_OK)
        {
            destination = data.getStringExtra(Util.DESTINATION);
            destinationEditText.setText(destination);
            destinationLatitude = data.getDoubleExtra(Util.DESTINATION_LATITUDE, 0);
            destinationLongitude = data.getDoubleExtra(Util.DESTINATION_LONGITUDE, 0);
        }
    }

    // if user doesn't select a photo, set a default image for the user
    public void setDefaultImage()
    {
        Bitmap defaultPic = BitmapFactory.decodeResource(getResources(), R.drawable.box);
        goodImageBytes = Util.getBytesArrayFromBitmap(defaultPic);
        goodImageView.setImageBitmap(defaultPic);
    }

    // on click listener for location
    public void locationClick(View view)
    {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("requestCode", Util.PICK_LOCATION_REQUEST);
        startActivityForResult(intent, Util.PICK_LOCATION_REQUEST);
    }

    // on click listener for destination
    public void destinationClick(View view)
    {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("requestCode", Util.PICK_DESTINATION_REQUEST);
        startActivityForResult(intent, Util.PICK_DESTINATION_REQUEST);
    }

    // on click listener for next button
    public void nextClick(View view)
    {
        receiverName = receiverNameEditText.getText().toString();

        // set to default image if user didn't choose any image
        if (goodImageBytes == null)
        {
            setDefaultImage();
        }

        // mandatory data
        if (receiverName == "" || pickUpDate == null || pickUpTime == null || pickUpLocation == "" || destination == "")
        {
            Util.createToast(getApplicationContext(), "Please fill in all required fields!");
        }
        else
        {
            Intent intent = new Intent(DeliveryActivity.this, DeliveryGoodsActivity.class);
            intent.putExtra(Util.RECEIVER_NAME, receiverName);
            intent.putExtra(Util.DATE, pickUpDate);
            intent.putExtra(Util.TIME, pickUpTime);
            intent.putExtra(Util.LOCATION, pickUpLocation);
            intent.putExtra(Util.LOCATION_LATITUDE, locationLatitude);
            intent.putExtra(Util.LOCATION_LONGITUDE, locationLongitude);
            intent.putExtra(Util.DESTINATION, destination);
            intent.putExtra(Util.DESTINATION_LATITUDE, destinationLatitude);
            intent.putExtra(Util.DESTINATION_LONGITUDE, destinationLongitude);
            intent.putExtra(Util.GOOD_IMAGE, goodImageBytes);
            startActivity(intent);
            finish();
        }
    }

    // on click listener for time button, used to open up a clock for user to pick a time
    public void popTimePicker(View view)
    {
        // listener if user clicks on a time slot
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;

                // constructing time string
                if (hour == 0) {
                    hour += 12;
                    format = "AM";
                } else if (hour == 12) {
                    format = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }

                StringBuilder time = new StringBuilder();
                time.append(String.format("%02d:%02d", hour, minute)).append(" ").append(format);

                pickUpTime = time.toString();
                timeTextView.setText(pickUpTime);
            }
        };

        // to open up the clock view
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, false);
        timePickerDialog.show();
        }
    }