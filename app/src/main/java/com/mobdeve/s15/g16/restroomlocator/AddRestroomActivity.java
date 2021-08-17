package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddRestroomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restroom);

        TextView tvStartTime = findViewById(R.id.tvOperatingStart);
        TextView tvEndTime = findViewById(R.id.tvOperatingEnd);

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curr_time = Calendar.getInstance();
                int hour = curr_time.get(Calendar.HOUR_OF_DAY);
                int minute = curr_time.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddRestroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minuteString;
                        if(minute < 10) {
                            minuteString = "0" + String.valueOf(minute);
                        }
                        else{
                            minuteString = String.valueOf(minute);
                        }
                        tvStartTime.setText(hourOfDay + ":" + minuteString);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curr_time = Calendar.getInstance();
                int hour = curr_time.get(Calendar.HOUR_OF_DAY);
                int minute = curr_time.get(Calendar.MINUTE);
                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddRestroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minuteString;
                        if(minute < 10) {
                            minuteString = "0" + String.valueOf(minute);
                        }
                        else{
                            minuteString = String.valueOf(minute);
                        }
                        tvEndTime.setText(hourOfDay + ":" + minuteString);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }
}