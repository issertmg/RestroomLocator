package com.mobdeve.s15.g16.restroomlocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ViewReviewDetailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review_details);

    }

    // Inflate options menu located on ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_details_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_review:
                // TODO action when edit is clicked
                return true;
            case R.id.action_delete_review:
                // TODO action when delete is clicked
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
