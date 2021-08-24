package com.mobdeve.s15.g16.restroomlocator;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivReviewImageOne, ivReviewImageTwo, ivReviewImageThree;
    private TextView tvReviewUsername, tvReviewNumber, tvReviewName;
    private RatingBar rbReviewRating;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.ivReviewImageOne = itemView.findViewById(R.id.ivReviewImageOne);
        this.ivReviewImageTwo = itemView.findViewById(R.id.ivReviewImageTwo);
        this.ivReviewImageThree = itemView.findViewById(R.id.ivReviewImageThree);
        this.tvReviewUsername = itemView.findViewById(R.id.tvReviewUsername);
        //this.tvReviewNumber = itemView.findViewById(R.id.tvReviewNumber);
        //this.tvReviewName = itemView.findViewById(R.id.tvReviewName);
        //this.rbReviewRating = itemView.findViewById(R.id.rbReviewRating);
    }

    public void setIvReviewImageOne(int iv) {this.ivReviewImageOne.setImageResource(iv);}
    public void setIvReviewImageTwo(int iv) {this.ivReviewImageTwo.setImageResource(iv);}
    public void setIvReviewImageThree(int iv) {this.ivReviewImageThree.setImageResource(iv);}
    public void setTvReviewUsername(String tv) {this.tvReviewUsername.setText(tv);}
    public void setTvReviewNumber(int n) {this.tvReviewNumber.setText(String.valueOf(n) + " reviews");}
    public void setTvReviewName(String tv) {this.tvReviewUsername.setText(tv);}
    public void setRbReviewRating(int n) {this.rbReviewRating.setNumStars(n);}
}
