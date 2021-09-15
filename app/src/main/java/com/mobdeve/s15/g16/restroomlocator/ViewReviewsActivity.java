package com.mobdeve.s15.g16.restroomlocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mobdeve.s15.g16.restroomlocator.adapters.MyReviewAdapter;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.mobdeve.s15.g16.restroomlocator.utils.ActivityNames;
import com.mobdeve.s15.g16.restroomlocator.utils.IntentKeys;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class ViewReviewsActivity extends AppCompatActivity {

    // RecyclerView Components
    private RecyclerView recyclerView;

    // Replacement of the base adapter view
    private MyReviewAdapter myReviewAdapter;
    private FloatingActionButton addReviewBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        this.recyclerView = findViewById(R.id.recyclerView);
        this.addReviewBtn = findViewById(R.id.fabAddReview);

        if (MyFirestoreHelper.isGuestUser())
            this.addReviewBtn.setVisibility(View.GONE);

        Intent i = getIntent();
        String restroomId = i.getStringExtra(IntentKeys.RESTROOM_ID_KEY);

        // Get reviews from the collection
        Query query = MyFirestoreReferences
                .getReviewCollectionReference()
                .whereEqualTo(MyFirestoreReferences.RESTROOMID_FIELD, restroomId)
                .orderBy(MyFirestoreReferences.TIMESTAMP_FIELD, Query.Direction.DESCENDING);

        // RECYCLER VIEW
        // Define options
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        // Define adapter
        this.myReviewAdapter = new MyReviewAdapter(options, this);

        this.recyclerView.setAdapter(this.myReviewAdapter);

        // Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.recyclerView.setLayoutManager(linearLayoutManager);

        this.addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewReviewsActivity.this, AddRestroomActivity.class);
                i.putExtra(IntentKeys.RESTROOM_ID_KEY, restroomId);
                i.putExtra(IntentKeys.EDITING_KEY, false);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // When our app is open, we need to have the adapter listening for any changes in the data.
        // To do so, we'd want to turn on the listening using the appropriate method in the onStart
        // or onResume (basically before the start but within the loop)
        this.myReviewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // We want to eventually stop the listening when we're about to exit an app as we don't need
        // something listening all the time in the background.
        this.myReviewAdapter.stopListening();
    }
}
