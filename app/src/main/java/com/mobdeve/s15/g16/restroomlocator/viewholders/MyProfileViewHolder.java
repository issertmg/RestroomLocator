package com.mobdeve.s15.g16.restroomlocator.viewholders;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.mobdeve.s15.g16.restroomlocator.utils.Helper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class MyProfileViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivReviewImageOne, ivReviewImageTwo, ivReviewImageThree;
    private TextView tvReviewUsername, tvReviewTimestamp;
    private HorizontalScrollView imageScrollView;

    public MyProfileViewHolder(View itemView) {
        super(itemView);
        this.tvReviewUsername = itemView.findViewById(R.id.tvReviewUsername);
        this.tvReviewTimestamp = itemView.findViewById(R.id.tvReviewTimestamp);
        this.ivReviewImageOne = itemView.findViewById(R.id.ivReviewImageOne);
        this.ivReviewImageTwo = itemView.findViewById(R.id.ivReviewImageTwo);
        this.ivReviewImageThree = itemView.findViewById(R.id.ivReviewImageThree);
        this.imageScrollView = itemView.findViewById(R.id.imageScrollView);
    }

    public void bindData(Review r) {

        if (r.getImageUri1().equals(MyFirestoreReferences.NOIMAGE))
            this.ivReviewImageOne.setVisibility(View.GONE);
        else
            this.ivReviewImageOne.setVisibility(View.VISIBLE);

        if (r.getImageUri2().equals(MyFirestoreReferences.NOIMAGE))
            this.ivReviewImageTwo.setVisibility(View.GONE);
        else
            this.ivReviewImageTwo.setVisibility(View.VISIBLE);

        if (r.getImageUri3().equals(MyFirestoreReferences.NOIMAGE))
            this.ivReviewImageThree.setVisibility(View.GONE);
        else
            this.ivReviewImageThree.setVisibility(View.VISIBLE);

        if (r.getImageUri1().equals(MyFirestoreReferences.NOIMAGE) &&
                r.getImageUri2().equals(MyFirestoreReferences.NOIMAGE) &&
                r.getImageUri3().equals(MyFirestoreReferences.NOIMAGE))
            this.imageScrollView.setVisibility(View.GONE);
        else
            this.imageScrollView.setVisibility(View.VISIBLE);

        MyFirestoreHelper.downloadImageIntoImageView(r.getId(),
                r.getImageUri1(), r.getImageUri2(), r.getImageUri3(),
                this.ivReviewImageOne, this.ivReviewImageTwo, this.ivReviewImageThree);
        this.tvReviewUsername.setText(r.getUsername());
        this.tvReviewTimestamp.setText(Helper.dateToString(r.getTimestamp()));
    }
}

