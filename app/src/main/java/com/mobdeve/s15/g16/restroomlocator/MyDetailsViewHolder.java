package com.mobdeve.s15.g16.restroomlocator;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s15.g16.restroomlocator.models.Comment;

public class MyDetailsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvComment, tvCommentUsername, tvCommentDot, tvCommentNumReviews;

    public MyDetailsViewHolder(View itemView) {
        super(itemView);

        this.tvComment = itemView.findViewById(R.id.tvComment);
        this.tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
        this.tvCommentDot = itemView.findViewById(R.id.tvCommentDot);   // FIXME: not sure if needed
        this.tvCommentNumReviews = itemView.findViewById(R.id.tvCommentNumReviews); // FIXME: CHANGE TO TIMESTAMP?
    }

    public void bindData(Comment c) {
        this.tvComment.setText(c.getMessage());
    }

}
