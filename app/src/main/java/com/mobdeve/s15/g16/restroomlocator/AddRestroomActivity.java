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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class AddRestroomActivity extends AppCompatActivity {
    private TextView tvStartTime, tvEndTime;
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
//                        String path = imageUriOne.getPath();
//                        File file = new File(path);

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
//                        String path = imageUri.getPath();
//                        File file = new File(path);

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
//                        String path = imageUri.getPath();
//                        File file = new File(path);

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

        // get location from intent
        latitude = getIntent().getDoubleExtra(ViewRestroomsNearbyActivity.LATITUDE, 0.0);
        longitude = getIntent().getDoubleExtra(ViewRestroomsNearbyActivity.LONGITUDE, 0.0);

        // initializing views
        tvStartTime = findViewById(R.id.tvOperatingStart);
        tvEndTime = findViewById(R.id.tvOperatingEnd);
        etvName = findViewById(R.id.etvName);
        etvPrice = findViewById(R.id.etvPrice);
        etvRemarks = findViewById(R.id.etvRemarks);
        btnAdd = findViewById(R.id.btnAdd);
        ibImgOne = findViewById(R.id.ibImgOne);
        ibImgTwo = findViewById(R.id.ibImgTwo);
        ibImgThree = findViewById(R.id.ibImgThree);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        // TODO: resolve authentication issues; currently using anonymous for testing
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create review
                        // TODO: properly get userId and restroomId

                        // check which images are null
                        Boolean imgOneIsNull = imageUriOne == null;
                        Boolean imgTwoIsNull = imageUriTwo == null;
                        Boolean imgThreeIsNull = imageUriThree == null;

                        // check for empty fields
                        String tempImgOne;
                        String tempImgTwo;
                        String tempImgThree;

                        if(imgOneIsNull)
                            tempImgOne = "NOIMAGE";
                        else
                            tempImgOne = imageUriOne.toString();

                        if(imgTwoIsNull)
                            tempImgTwo = "NOIMAGE";
                        else
                            tempImgTwo = imageUriTwo.toString();

                        if(imgThreeIsNull)
                            tempImgThree = "NOIMAGE";
                        else
                            tempImgThree = imageUriThree.toString();

                        Review r = new Review(
                            "xxxxxxxxxxxxxxxxxxxx",
                            "xxxxxxxxxxxxxxxxxxxx",
                            tvStartTime.getText().toString(),
                            tvEndTime.getText().toString(),
                            etvPrice.getText().toString(),
                            tempImgOne,
                            tempImgTwo,
                            tempImgThree,
                            etvRemarks.getText().toString()
                        );

                        // get Reviews collection reference
                        CollectionReference reviewsRef = MyFirestoreReferences.getReviewCollectionReference();

                        // add review
                        Task t1 = reviewsRef.add(r)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {


                                    // at least one image was selected, upload images to Storage
                                    if(!(imgOneIsNull && imgTwoIsNull && imgThreeIsNull)){
                                        // upload the images
                                        if(!imgOneIsNull){
                                            StorageReference imageRefOne = MyFirestoreReferences.getStorageReferenceInstance()
                                                    .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriOne));
                                            imageRefOne.putFile(imageUriOne)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Log.w(TAG, "imgOne upload successful");
                                                        }
                                                    });
                                        };

                                        if(!imgTwoIsNull){
                                            StorageReference imageRefTwo = MyFirestoreReferences.getStorageReferenceInstance()
                                                    .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriTwo));
                                            imageRefTwo.putFile(imageUriTwo)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Log.w(TAG, "imgTwo upload successful");
                                                        }
                                                    });
                                        }

                                        if(!imgThreeIsNull){
                                            StorageReference imageRefThree = MyFirestoreReferences.getStorageReferenceInstance()
                                                    .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriThree));
                                            imageRefThree.putFile(imageUriThree)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Log.w(TAG, "imgThree upload successful");
                                                        }
                                                    });
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                    }
                });
            }
        });
    }
}