package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.mobdeve.s15.g16.restroomlocator.adapters.MyProfileAdapter;
import com.mobdeve.s15.g16.restroomlocator.adapters.MyReviewAdapter;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvAgeValue, tvReviewNum;
    private RecyclerView recyclerView;
    private MyProfileAdapter myProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.tvUsername = findViewById(R.id.tvUsername);
        getSupportActionBar().setTitle("My Profile");

        this.tvAgeValue = findViewById(R.id.tvAgeValue);
        this.tvReviewNum = findViewById(R.id.tvNReviewsValue);
        this.recyclerView = findViewById(R.id.recyclerView);

        this.setTvNumReviews(0);

        Query query = MyFirestoreReferences
                .getReviewCollectionReference()
                .whereEqualTo(MyFirestoreReferences.USERID_FIELD, MyFirestoreHelper.getUserID())
                .orderBy(MyFirestoreReferences.TIMESTAMP_FIELD, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        this.myProfileAdapter = new MyProfileAdapter(options, this);

        this.recyclerView.setAdapter(this.myProfileAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.recyclerView.setLayoutManager(linearLayoutManager);

        MyFirestoreHelper.displayUserDetails(this);
    }

    public void setTvUsername(String username) {
        tvUsername.setText(username);
    }

    public void setTvAgeValue(String dateAge) {
        tvAgeValue.setText("Joined " + dateAge);
    }

    public void setTvNumReviews(int numReviews) {
        tvReviewNum.setText("(" + numReviews + ")");
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.myProfileAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.myProfileAdapter.stopListening();
    }
}