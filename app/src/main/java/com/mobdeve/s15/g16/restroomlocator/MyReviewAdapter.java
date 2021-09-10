package com.mobdeve.s15.g16.restroomlocator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MyReviewAdapter extends FirestoreRecyclerAdapter<Review, MyReviewViewHolder> {

    public MyReviewAdapter(FirestoreRecyclerOptions<Review> options, String username) {
        super(options);
    }

    @Override
    public MyReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        MyReviewViewHolder myReviewViewHolder = new MyReviewViewHolder(view);
        return myReviewViewHolder;
    }

    // The onBindViewHolder is slightly different as you also get the "model". It was clear from the
    // documentation, but it seems that its discouraging the use of the position parameter. The
    // model passed in is actually the respective model that is about to be bound. Hence, why we
    // don't use position, and directly get the information from the model parameter.
    @Override
    protected void onBindViewHolder(MyReviewViewHolder holder, int position, Review r) {

        //FIXME: (SUGGESTION for username): query the username using the userID from review
        // String username = (query for username based on the userID from review)


        //FIXME: suggestion of ivy: holder.bindDate(review, username)

    }
}
