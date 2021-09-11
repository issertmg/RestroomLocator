package com.mobdeve.s15.g16.restroomlocator.utils;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    private static DateFormat dateFormatterInstance = null;

    private static DateFormat getDateFormatterInstance() {
        if(dateFormatterInstance == null) {
            dateFormatterInstance = new SimpleDateFormat("dd-mm-yyyy hh:mm");
        }
        return dateFormatterInstance;
    }

    public static String dateToString(Date date) {
        return getDateFormatterInstance().format(date);
    }
}
