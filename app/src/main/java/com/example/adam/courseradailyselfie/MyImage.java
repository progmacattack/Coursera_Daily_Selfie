package com.example.adam.courseradailyselfie;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Adam on 4/30/2016.
 * This keeps track of titles and paths of images used in main activity
 */
public class MyImage {
    private String title;
    private String path;
    private Calendar cal = new GregorianCalendar();

    //path is the path of the image
    public MyImage(Context context, String path) {
        this.path = path;
        this.title = DateUtils.formatDateTime(context, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }
}
