package com.mobdeve.s15.g16.restroomlocator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mobdeve.s15.g16.restroomlocator.models.User;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

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

        MyFirestoreReferences.getUserCollectionReference()
                .document(r.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User temp = documentSnapshot.toObject(User.class);
                        holder.bindData(r, temp.getUsername());

                    }
                });
    }
}
