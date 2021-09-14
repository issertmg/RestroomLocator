package com.mobdeve.s15.g16.restroomlocator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mobdeve.s15.g16.restroomlocator.adapters.MyDetailsAdapter;
import com.mobdeve.s15.g16.restroomlocator.models.Comment;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.mobdeve.s15.g16.restroomlocator.utils.ActivityNames;
import com.mobdeve.s15.g16.restroomlocator.utils.Helper;
import com.mobdeve.s15.g16.restroomlocator.utils.IntentKeys;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ViewReviewDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ViewRevDetailsActivity";

    // intent details
    private String reviewId, username, startTime, endTime, fee, imageUri1, imageUri2, imageUri3, remarks, timestamp;

    // Views needed
    private TextView tvDetailsUsername, tvDetailsTimestamp, tvDetailsName,
            tvDetailsRemarksInfo, tvDetailsFeeInfo, tvDetailsHoursInfo; //FIXME:
    private EditText etComment;
    private ImageButton ibAddComment;
    private ImageView ivDetailsImageOne, ivDetailsImageTwo, ivDetailsImageThree;
    private HorizontalScrollView imageScrollView;

    // RecyclerView Components
    private RecyclerView recyclerView;

    // Replacement of the base adapter view
    private MyDetailsAdapter myDetailsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review_details);

        this.recyclerView = findViewById(R.id.rvComments);
        this.tvDetailsUsername = findViewById(R.id.tvDetailsUsername);
        this.tvDetailsTimestamp = findViewById(R.id.tvDetailsTimestamp);
        this.tvDetailsName = findViewById(R.id.tvDetailsName);
        this.tvDetailsRemarksInfo = findViewById(R.id.tvDetailsRemarksInfo);
        this.tvDetailsFeeInfo = findViewById(R.id.tvDetailsFeeInfo);
        this.tvDetailsHoursInfo = findViewById(R.id.tvDetailsHoursInfo);
        this.etComment = findViewById(R.id.etComment);
        this.ibAddComment = findViewById(R.id.ibAddComment);
        this.ivDetailsImageOne = findViewById(R.id.ivDetailsImageOne);
        this.ivDetailsImageTwo = findViewById(R.id.ivDetailsImageTwo);
        this.ivDetailsImageThree = findViewById(R.id.ivDetailsImageThree);
        this.imageScrollView = findViewById(R.id.hsvImages);


        Intent i = getIntent();
        reviewId = i.getStringExtra(IntentKeys.REVIEW_ID_KEY);
        username = i.getStringExtra(IntentKeys.USERNAME_KEY);
        startTime = i.getStringExtra(IntentKeys.START_TIME_KEY);
        endTime = i.getStringExtra(IntentKeys.END_TIME_KEY);
        fee = i.getStringExtra(IntentKeys.FEE_KEY);
        imageUri1 = i.getStringExtra(IntentKeys.IMAGE_URI_1_KEY);
        imageUri2 = i.getStringExtra(IntentKeys.IMAGE_URI_2_KEY);
        imageUri3 = i.getStringExtra(IntentKeys.IMAGE_URI_3_KEY);
        remarks = i.getStringExtra(IntentKeys.REMARKS_KEY);
        timestamp = i.getStringExtra(IntentKeys.TIMESTAMP_KEY);

        this.tvDetailsUsername.setText(username);
        this.tvDetailsTimestamp.setText(timestamp);
        this.tvDetailsRemarksInfo.setText(remarks);
        this.tvDetailsFeeInfo.setText(fee);
        this.tvDetailsHoursInfo.setText(startTime + "H - " + endTime + "H");
        displayImages(reviewId, imageUri1, imageUri2, imageUri3);

        //TODO
        this.tvDetailsName.setVisibility(View.GONE);


        // Get comments from the collection
        Query query = MyFirestoreReferences
                .getCommentCollectionReference()
                .whereEqualTo(MyFirestoreReferences.REVIEWID_FIELD, reviewId)
                .orderBy(MyFirestoreReferences.TIMESTAMP_FIELD, Query.Direction.ASCENDING);

        // RECYCLER VIEW
        // Define options
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        // Define adapter
        this.myDetailsAdapter = new MyDetailsAdapter(options);

        this.recyclerView.setAdapter(this.myDetailsAdapter);

        // Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.recyclerView.setLayoutManager(linearLayoutManager);

        this.ibAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();
                String userId = MyFirestoreHelper.getUserID();
                String username = MyFirestoreHelper.getUsername();

                // Send the data off to the Comment collection
                MyFirestoreReferences.getCommentCollectionReference().add(new Comment(userId, username, reviewId, comment))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                // "Reset" the comment in the EditText
                                etComment.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Log.d("HUHU", "WHY");
                            }
                        });
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MyFirestoreReferences.getReviewCollectionReference().document(reviewId).get()
                .addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Review r = task.getResult().toObject(Review.class);
                            tvDetailsUsername.setText(r.getUsername());
                            tvDetailsTimestamp.setText(Helper.dateToString(r.getTimestamp()));
                            tvDetailsRemarksInfo.setText(r.getRemarks());
                            tvDetailsFeeInfo.setText(r.getFee());
                            tvDetailsHoursInfo.setText(r.getStartTime() + "H - " + r.getEndTime() + "H");
                            displayImages(reviewId, r.getImageUri1(), r.getImageUri2(), r.getImageUri3());
                        }
                    }
                });
    }

    // Inflate options menu located on ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_details_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_review:
                Intent i = new Intent(this, AddRestroomActivity.class);
                i.putExtra(IntentKeys.EDITING_KEY, true);
                i.putExtra(IntentKeys.REVIEW_ID_KEY, reviewId);
                i.putExtra(IntentKeys.START_TIME_KEY, startTime);
                i.putExtra(IntentKeys.END_TIME_KEY, endTime);
                i.putExtra(IntentKeys.FEE_KEY, fee);
                i.putExtra(IntentKeys.REMARKS_KEY, remarks);
                i.putExtra(IntentKeys.IMAGE_URI_1_KEY, imageUri1);
                i.putExtra(IntentKeys.IMAGE_URI_2_KEY, imageUri2);
                i.putExtra(IntentKeys.IMAGE_URI_3_KEY, imageUri3);
                startActivity(i);
                return true;
            case R.id.action_delete_review:
                MyFirestoreHelper.deleteReview(this, reviewId, imageUri1, imageUri2, imageUri3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayImages(String reviewId, String imageUri1, String imageUri2, String imageUri3) {
        if (imageUri1.equals(MyFirestoreReferences.NOIMAGE))
            this.ivDetailsImageOne.setVisibility(View.GONE);
        else
            this.ivDetailsImageOne.setVisibility(View.VISIBLE);

        if (imageUri2.equals(MyFirestoreReferences.NOIMAGE))
            this.ivDetailsImageTwo.setVisibility(View.GONE);
        else
            this.ivDetailsImageTwo.setVisibility(View.VISIBLE);

        if (imageUri3.equals(MyFirestoreReferences.NOIMAGE))
            this.ivDetailsImageThree.setVisibility(View.GONE);
        else
            this.ivDetailsImageThree.setVisibility(View.VISIBLE);

        if (imageUri1.equals(MyFirestoreReferences.NOIMAGE) &&
                imageUri2.equals(MyFirestoreReferences.NOIMAGE) &&
                imageUri3.equals(MyFirestoreReferences.NOIMAGE))
            this.imageScrollView.setVisibility(View.GONE);
        else
            this.imageScrollView.setVisibility(View.VISIBLE);

        MyFirestoreHelper.downloadImageIntoImageView(reviewId,
                imageUri1, imageUri2, imageUri3,
                this.ivDetailsImageOne, this.ivDetailsImageTwo, this.ivDetailsImageThree);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.myDetailsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.myDetailsAdapter.stopListening();
    }
}
