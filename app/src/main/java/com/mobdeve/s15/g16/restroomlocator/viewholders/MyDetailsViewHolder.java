package com.mobdeve.s15.g16.restroomlocator.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Comment;
import com.mobdeve.s15.g16.restroomlocator.utils.Helper;

public class MyDetailsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvComment, tvCommentUsername, tvCommentTimestamp;

    public MyDetailsViewHolder(View itemView) {
        super(itemView);

        this.tvComment = itemView.findViewById(R.id.tvComment);
        this.tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
        this.tvCommentTimestamp = itemView.findViewById(R.id.tvCommentTimestamp);
    }

    public void bindData(Comment c) {
        this.tvComment.setText(c.getMessage());
        this.tvCommentUsername.setText(c.getUsername());
        this.tvCommentTimestamp.setText(Helper.dateToString(c.getTimestamp()));
    }

}
