package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvAgeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvAgeValue = findViewById(R.id.tvAgeValue);

        MyFirestoreHelper.displayUserDetails(this);
    }

    public void setTvUsername(String username) {
        tvUsername.setText(username);
    }

    public void setTvAgeValue(String dateAge) {
        tvAgeValue.setText("Joined " + dateAge);
    }
}