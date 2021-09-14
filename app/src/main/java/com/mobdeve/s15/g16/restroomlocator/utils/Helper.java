package com.mobdeve.s15.g16.restroomlocator.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class Helper {
    private static DateFormat dateFormatterInstance = null;

    private static DateFormat getDateFormatterInstance() {
        if(dateFormatterInstance == null) {
            dateFormatterInstance = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            dateFormatterInstance.setTimeZone(TimeZone.getTimeZone("Etc/GMT-8"));
        }
        return dateFormatterInstance;
    }

    public static String dateToString(Date date) {
        return getDateFormatterInstance().format(date);
    }

}
