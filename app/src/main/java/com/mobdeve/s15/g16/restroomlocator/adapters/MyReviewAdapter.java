package com.mobdeve.s15.g16.restroomlocator.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s15.g16.restroomlocator.ViewReviewDetailsActivity;
import com.mobdeve.s15.g16.restroomlocator.ViewReviewsActivity;
import com.mobdeve.s15.g16.restroomlocator.utils.Helper;
import com.mobdeve.s15.g16.restroomlocator.utils.IntentKeys;
import com.mobdeve.s15.g16.restroomlocator.viewholders.MyReviewViewHolder;
import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mobdeve.s15.g16.restroomlocator.models.User;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class MyReviewAdapter extends FirestoreRecyclerAdapter<Review, MyReviewViewHolder> {

    private Context context;

    public MyReviewAdapter(FirestoreRecyclerOptions<Review> options, Context context) {
        super(options);
        this.context = context;
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
        holder.bindData(r);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewReviewDetailsActivity.class);
                i.putExtra(IntentKeys.REVIEW_ID_KEY, r.getId());
                i.putExtra(IntentKeys.USERNAME_KEY, r.getUsername());
                i.putExtra(IntentKeys.START_TIME_KEY, r.getStartTime());
                i.putExtra(IntentKeys.END_TIME_KEY, r.getEndTime());
                i.putExtra(IntentKeys.FEE_KEY, r.getFee());
                i.putExtra(IntentKeys.IMAGE_URI_1_KEY, r.getImageUri1());
                i.putExtra(IntentKeys.IMAGE_URI_2_KEY, r.getImageUri2());
                i.putExtra(IntentKeys.IMAGE_URI_3_KEY, r.getImageUri3());
                i.putExtra(IntentKeys.REMARKS_KEY, r.getRemarks());
                i.putExtra(IntentKeys.TIMESTAMP_KEY, Helper.dateToString(r.getTimestamp()));
                ((Activity) context).startActivity(i);
            }
        });
    }
}
