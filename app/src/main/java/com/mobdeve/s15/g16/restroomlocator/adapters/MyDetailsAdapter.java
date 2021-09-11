package com.mobdeve.s15.g16.restroomlocator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s15.g16.restroomlocator.viewholders.MyDetailsViewHolder;
import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Comment;
import com.mobdeve.s15.g16.restroomlocator.models.User;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class MyDetailsAdapter extends FirestoreRecyclerAdapter<Comment, MyDetailsViewHolder> {

    public MyDetailsAdapter(FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    public MyDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        MyDetailsViewHolder myDetailsViewHolder = new MyDetailsViewHolder(view);
        return myDetailsViewHolder;
    }

    @Override
    protected void onBindViewHolder(MyDetailsViewHolder holder, int position, Comment c) {
        holder.bindData(c);
    }
}
