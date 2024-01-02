package com.example.fbvideodownloader.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {

    public static String formatTime(long milliseconds) {
        SimpleDateFormat sdf;

        long seconds = milliseconds / 1000;

        if (seconds < 3600000) {
            sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }

        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        return sdf.format(seconds);
    }

}

