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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s15.g16.restroomlocator.models.Restroom;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.mobdeve.s15.g16.restroomlocator.utils.ActivityNames;
import com.mobdeve.s15.g16.restroomlocator.utils.IntentKeys;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

import java.util.Calendar;

public class AddRestroomActivity extends AppCompatActivity {
    private TextView tvAddRestroom, tvStartTime, tvEndTime;
    private EditText etvName, etvPrice, etvRemarks;
    private ImageButton ibImgOne, ibImgTwo, ibImgThree;
    private Button btnAdd;
    private int padding_dp = 2;
    private int padding_px;
    private Uri imageUriOne = null;
    private Uri imageUriTwo = null;
    private Uri imageUriThree = null;
    public final String TAG = "AddRestroomActivity";

    // For storing location from intent
    private String restroomId;
    private double latitude;
    private double longitude;

    private ActivityResultLauncher<Intent> galleryActivityResultLauncherOne = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUriOne = data.getData();

                        ibImgOne.setImageURI(imageUriOne);
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
                        imageUriTwo = data.getData();

                        ibImgTwo.setImageURI(imageUriTwo);
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
                        imageUriThree = data.getData();

                        ibImgThree.setImageURI(imageUriThree);
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

        // initializing views
        tvAddRestroom = findViewById(R.id.tvAddRestroom);
        tvStartTime = findViewById(R.id.tvOperatingStart);
        tvEndTime = findViewById(R.id.tvOperatingEnd);
        etvName = findViewById(R.id.etvName);
        etvPrice = findViewById(R.id.etvPrice);
        etvRemarks = findViewById(R.id.etvRemarks);
        btnAdd = findViewById(R.id.btnAdd);
        ibImgOne = findViewById(R.id.ibImgOne);
        ibImgTwo = findViewById(R.id.ibImgTwo);
        ibImgThree = findViewById(R.id.ibImgThree);

        // get location from intent
        restroomId = getIntent().getStringExtra(IntentKeys.RESTROOM_ID_KEY);
        latitude = getIntent().getDoubleExtra(IntentKeys.LATITUDE_KEY, 0.0);
        longitude = getIntent().getDoubleExtra(IntentKeys.LONGITUDE_KEY, 0.0);

        // check if intent is for editing
        boolean editing = getIntent().getBooleanExtra(IntentKeys.EDITING_KEY, false);
        String reviewId = getIntent().getStringExtra(IntentKeys.REVIEW_ID_KEY);

        // hide restroom name field if restroom location already exists
        if (restroomId != null)
            etvName.setVisibility(View.GONE);

        // making TimePicker dialog pop up
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

        // submitting a review
        if(!editing){
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // check which images are null
                    Boolean imgOneIsNull = imageUriOne == null;
                    Boolean imgTwoIsNull = imageUriTwo == null;
                    Boolean imgThreeIsNull = imageUriThree == null;

                    // check for empty fields
                    String tempImgOne;
                    String tempImgTwo;
                    String tempImgThree;

                    if(imgOneIsNull)
                        tempImgOne = MyFirestoreReferences.NOIMAGE;
                    else
                        tempImgOne = imageUriOne.toString();

                    if(imgTwoIsNull)
                        tempImgTwo = MyFirestoreReferences.NOIMAGE;
                    else
                        tempImgTwo = imageUriTwo.toString();

                    if(imgThreeIsNull)
                        tempImgThree = MyFirestoreReferences.NOIMAGE;
                    else
                        tempImgThree = imageUriThree.toString();

                    // Get user id
                    String userId = MyFirestoreHelper.getUserID();
                    String username = MyFirestoreHelper.getUsername();

                    if (restroomId == null) {
                        Restroom location = new Restroom(
                                etvName.getText().toString(),
                                latitude,
                                longitude
                        );

                        Review review = new Review(
                                userId,
                                username,
                                "",
                                tvStartTime.getText().toString(),
                                tvEndTime.getText().toString(),
                                etvPrice.getText().toString(),
                                tempImgOne,
                                tempImgTwo,
                                tempImgThree,
                                etvRemarks.getText().toString()
                        );

                        MyFirestoreHelper.createRestroomLocation(
                                location,
                                review,
                                imgOneIsNull,
                                imgTwoIsNull,
                                imgThreeIsNull,
                                imageUriOne,
                                imageUriTwo,
                                imageUriThree,
                                AddRestroomActivity.this,
                                ActivityNames.VIEW_RESTROOMS_NEARBY_ACTIVITY);
                    }
                    else {
                        Review review = new Review(
                                userId,
                                username,
                                restroomId,
                                tvStartTime.getText().toString(),
                                tvEndTime.getText().toString(),
                                etvPrice.getText().toString(),
                                tempImgOne,
                                tempImgTwo,
                                tempImgThree,
                                etvRemarks.getText().toString()
                        );

                        MyFirestoreHelper.createReview(
                                review,
                                imgOneIsNull,
                                imgTwoIsNull,
                                imgThreeIsNull,
                                imageUriOne,
                                imageUriTwo,
                                imageUriThree,
                                AddRestroomActivity.this,
                                ActivityNames.VIEW_REVIEWS_ACTIVITY);
                    }
                }
            });
        }
        // editing a review
        else{
            // modify text fields appropriately
            tvAddRestroom.setText("Edit restroom");
            btnAdd.setText("Save changes");
            etvName.setVisibility(View.GONE);

            // load Review details into the view
            tvStartTime.setText(getIntent().getStringExtra(IntentKeys.START_TIME_KEY));
            tvEndTime.setText(getIntent().getStringExtra(IntentKeys.END_TIME_KEY));
            etvPrice.setText(getIntent().getStringExtra(IntentKeys.FEE_KEY));
            etvRemarks.setText(getIntent().getStringExtra(IntentKeys.REMARKS_KEY));
            MyFirestoreHelper.downloadImageIntoImageView(
                    reviewId,
                    getIntent().getStringExtra(IntentKeys.IMAGE_URI_1_KEY),
                    getIntent().getStringExtra(IntentKeys.IMAGE_URI_2_KEY),
                    getIntent().getStringExtra(IntentKeys.IMAGE_URI_3_KEY),
                    ibImgOne,
                    ibImgTwo,
                    ibImgThree
            );

//            DocumentReference reviewRef = MyFirestoreReferences.getReviewCollectionReference().document(reviewId);
//            reviewRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful()){
//                        DocumentSnapshot document = task.getResult();
//                        if(document.exists()){
//                            tvStartTime.setText(document.getString(MyFirestoreReferences.STARTTIME_FIELD));
//                            tvEndTime.setText(document.getString(MyFirestoreReferences.ENDTIME_FIELD));
//                            etvPrice.setText(document.getString(MyFirestoreReferences.FEE_FIELD));
//                            etvRemarks.setText(document.getString(MyFirestoreReferences.REMARKS_FIELD));
//                            MyFirestoreHelper.downloadImageIntoImageView(
//                                    reviewId,
//                                    document.getString(MyFirestoreReferences.IMAGEURI1_FIELD),
//                                    document.getString(MyFirestoreReferences.IMAGEURI2_FIELD),
//                                    document.getString(MyFirestoreReferences.IMAGEURI3_FIELD),
//                                    ibImgOne,
//                                    ibImgTwo,
//                                    ibImgThree
//                            );
//                        }
//                    }
//                }
//            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check which images are null
                    Boolean imgOneIsNull = imageUriOne == null;
                    Boolean imgTwoIsNull = imageUriTwo == null;
                    Boolean imgThreeIsNull = imageUriThree == null;

                    // check for empty fields
                    String tempImgOne;
                    String tempImgTwo;
                    String tempImgThree;

                    if(imgOneIsNull)
                        tempImgOne = MyFirestoreReferences.NOIMAGE;
                    else
                        tempImgOne = imageUriOne.toString();

                    if(imgTwoIsNull)
                        tempImgTwo = MyFirestoreReferences.NOIMAGE;
                    else
                        tempImgTwo = imageUriTwo.toString();

                    if(imgThreeIsNull)
                        tempImgThree = MyFirestoreReferences.NOIMAGE;
                    else
                        tempImgThree = imageUriThree.toString();

                    String userId = MyFirestoreHelper.getUserID();
                    String username = MyFirestoreHelper.getUsername();

                    Review review = new Review(
                            reviewId,
                            userId,
                            username,
                            restroomId,
                            tvStartTime.getText().toString(),
                            tvEndTime.getText().toString(),
                            etvPrice.getText().toString(),
                            tempImgOne,
                            tempImgTwo,
                            tempImgThree,
                            etvRemarks.getText().toString()
                    );
                    MyFirestoreHelper.editReview(
                            review,
                            imgOneIsNull,
                            imgTwoIsNull,
                            imgThreeIsNull,
                            imageUriOne,
                            imageUriTwo,
                            imageUriThree,
                            AddRestroomActivity.this
                    );
                }
            });
        }
    }
}