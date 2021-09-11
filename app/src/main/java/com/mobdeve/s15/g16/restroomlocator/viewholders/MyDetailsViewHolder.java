package com.mobdeve.s15.g16.restroomlocator.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Comment;

public class MyDetailsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvComment, tvCommentUsername, tvCommentNumReviews;

    public MyDetailsViewHolder(View itemView) {
        super(itemView);

        this.tvComment = itemView.findViewById(R.id.tvComment);
        this.tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
        this.tvCommentNumReviews = itemView.findViewById(R.id.tvCommentTimestamp);
    }

    public void bindData(Comment c, String username) {
        this.tvComment.setText(c.getMessage());
        this.tvCommentUsername.setText(username);
    }

}
