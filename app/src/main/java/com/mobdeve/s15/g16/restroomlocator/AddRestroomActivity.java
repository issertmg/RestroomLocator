package com.mobdeve.s15.g16.restroomlocator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;

public class AddRestroomActivity extends AppCompatActivity {
    private TextView tvStartTime, tvEndTime;
    private ImageButton ibImgOne, ibImgTwo, ibImgThree;
    private int padding_dp = 2;
    private int padding_px;

    // For storing location from intent
    private double latitude;
    private double longitude;

    private ActivityResultLauncher<Intent> galleryActivityResultLauncherOne = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        String path = imageUri.getPath();
                        File file = new File(path);

                        ibImgOne.setImageURI(imageUri);
                        ibImgOne.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ibImgOne.setPadding(padding_px, padding_px, padding_px, padding_px);
                    }
                }
            });

    private ActivityResultLauncher<Intent> galleryActivityResultLauncherTwo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        String path = imageUri.getPath();
                        File file = new File(path);

                        ibImgTwo.setImageURI(imageUri);
                        ibImgTwo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ibImgTwo.setPadding(padding_px, padding_px, padding_px, padding_px);
                    }
                }
            });

    private ActivityResultLauncher<Intent> galleryActivityResultLauncherThree = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        String path = imageUri.getPath();
                        File file = new File(path);

                        ibImgThree.setImageURI(imageUri);
                        ibImgThree.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ibImgThree.setPadding(padding_px, padding_px, padding_px, padding_px);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restroom);

        padding_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding_dp, AddRestroomActivity.this.getResources().getDisplayMetrics());

        // get location from intent
        latitude = getIntent().getDoubleExtra(ViewRestroomsNearbyActivity.LATITUDE, 0.0);
        longitude = getIntent().getDoubleExtra(ViewRestroomsNearbyActivity.LONGITUDE, 0.0);

        // setting TimePickerDialog to pop up on click
        tvStartTime = findViewById(R.id.tvOperatingStart);
        tvEndTime = findViewById(R.id.tvOperatingEnd);

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curr_time = Calendar.getInstance();
                int hour = curr_time.get(Calendar.HOUR_OF_DAY);
                int minute = curr_time.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddRestroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minuteString;
                        if(minute < 10) {
                            minuteString = "0" + String.valueOf(minute);
                        }
                        else{
                            minuteString = String.valueOf(minute);
                        }
                        tvStartTime.setText(hourOfDay + ":" + minuteString);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curr_time = Calendar.getInstance();
                int hour = curr_time.get(Calendar.HOUR_OF_DAY);
                int minute = curr_time.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddRestroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minuteString;
                        if(minute < 10) {
                            minuteString = "0" + String.valueOf(minute);
                        }
                        else{
                            minuteString = String.valueOf(minute);
                        }
                        tvEndTime.setText(hourOfDay + ":" + minuteString);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

        ibImgOne = findViewById(R.id.ibImgOne);
        ibImgTwo = findViewById(R.id.ibImgTwo);
        ibImgThree = findViewById(R.id.ibImgThree);

        // retrieving image from gallery
        ibImgOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                galleryActivityResultLauncherOne.launch(gallery_intent);
            }
        });

        ibImgTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                galleryActivityResultLauncherTwo.launch(gallery_intent);
            }
        });

        ibImgThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                galleryActivityResultLauncherThree.launch(gallery_intent);
            }
        });
    }
}