package com.mobdeve.s15.g16.restroomlocator.adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s15.g16.restroomlocator.MyViewHolder;
import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Review;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private ArrayList<Review> data;

    //FIXME:
    public MyAdapter(ArrayList data) { this.data = data; }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //TODO:

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
