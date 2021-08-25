package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FOR TESTING:
        Intent i = new Intent(MainActivity.this, ViewRestroomsNearbyActivity.class);
        startActivity(i);
        finish();

    }
}