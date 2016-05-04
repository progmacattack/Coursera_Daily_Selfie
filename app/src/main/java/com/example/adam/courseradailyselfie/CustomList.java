package com.example.adam.courseradailyselfie;

import android.graphics.Bitmap;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adam on 4/28/2016.
 * Adapter for the listview
 */
public class CustomList extends ArrayAdapter {
    private final Activity context;
    private ArrayList<String> textArray = new ArrayList<>();
    private ArrayList<Bitmap> imageArray = new ArrayList<>();
    public CustomList(Activity context, ArrayList<String> textArray, ArrayList<Bitmap> imageArray) {
        super(context, R.layout.pic_text_list, textArray);
        this.context = context;
        this.textArray = textArray;
        this.imageArray = imageArray;
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.pic_text_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(textArray.get(position));
        imageView.setImageBitmap(imageArray.get(position));
        return rowView;
    }

}
