package com.example.adam.courseradailyselfie;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class SelfieEveryDay extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private int piId = 123;
    private Intent alarmIntent;
    private MyImage currentImage;
    private ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    private ArrayList<String> stringArray = new ArrayList<String>();
    private ArrayList<String> bitmapPathArray = new ArrayList<String>();
    private Calendar cal = new GregorianCalendar();
    private ListView mListView;
    private ImageView mImageView;
    private ImageView mImageViewFull;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_every_day);
        this.mImageView = (ImageView) findViewById(R.id.img);
        this.mTextView = (TextView) findViewById(R.id.txt);
        this.mImageViewFull = (ImageView) findViewById(R.id.full_image);
        loadImageInfo();

        //set up alarm and notification
        alarmIntent = new Intent(SelfieEveryDay.this, AlarmReceiver.class);
        startAlarm();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selfie_every_day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.take_picture) {
            dispatchTakePictureIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentImage.getPath());
            bitmapPathArray.add(currentImage.getPath());
            bitmapArray.add(imageBitmap);
            stringArray.add(currentImage.getTitle());
            makeListView(stringArray, bitmapArray);
        }
    }

    //invoke intent to take a photo
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
    }

    private File createImageFile() throws IOException {
        // Create a unique image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //create image object with path and description for use elsewhere
        currentImage = new MyImage(this.getApplicationContext(), image.getAbsolutePath());
        galleryAddPic();
        return image;
    }

    //add pic to device media gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentImage.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onPause(){
        super.onPause();
        String imageNames = "";
        String imagePaths = "";
        //put arraylist in a string to allow to save state
        if (!isListEmpty()) {
            for (int i = 0; i < stringArray.size(); i++) {
                if (i == 0) {
                    imageNames = stringArray.get(i);
                    imagePaths = bitmapPathArray.get(i);
                } else {
                    imageNames += ";" + stringArray.get(i);
                    imagePaths = bitmapPathArray.get(i);
                }
            }
            for (int i = 0; i < bitmapPathArray.size(); i++) {
                if (i == 0) {
                    imagePaths = bitmapPathArray.get(i);
                } else {
                    imagePaths += ";" + bitmapPathArray.get(i);
                }
            }
        }

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("imageNames",imageNames);
        editor.putString("imagePaths",imagePaths);
        // Commit the edits!
        editor.commit();
    }

    //load images that were taken in previous sessions
    private void loadImageInfo() {
     if (isListEmpty()) {
         SharedPreferences sPrefInfo = getPreferences(0);
         String imageNames = sPrefInfo.getString("imageNames", null);
         String imagePaths = sPrefInfo.getString("imagePaths", null);
         if (imageNames != null && imagePaths != null) {
             stringArray = new ArrayList<String>(Arrays.asList(imageNames.split(";")));
             bitmapPathArray = new ArrayList<String>(Arrays.asList(imagePaths.split(";")));
             bitmapArray = new ArrayList<Bitmap>();
             for (String s : bitmapPathArray) {
                 Bitmap imageBitmap = BitmapFactory.decodeFile(s);
                 bitmapArray.add(imageBitmap);
             }
             makeListView(stringArray, bitmapArray);
         }
     }
    }

    //generate the list view
    private void makeListView(ArrayList<String> stringArr, ArrayList<Bitmap> bitmapArr) {
        final CustomList adapter = new CustomList(SelfieEveryDay.this, stringArr, bitmapArr);
        mListView = (ListView)findViewById(R.id.list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //mImageView.setImageBitmap(bitmapArray.get(position));
                final Dialog dialog = new Dialog(SelfieEveryDay.this);
                dialog.setContentView(R.layout.full_image_layout);
                dialog.setTitle(stringArray.get(position));
                mImageView = (ImageView) dialog.findViewById(R.id.full_image);
                mImageView.setImageBitmap(bitmapArray.get(position));
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private boolean isListEmpty() {
        if (stringArray.size() < 1) {
            return true;
        }
        else {
            return false;
        }
    }

    //start recurring alarm to remind user to take selfies zsccsdsawdaqesaqwedswergyyui
    public void startAlarm() {

        pendingIntent = PendingIntent.getBroadcast(SelfieEveryDay.this,0,alarmIntent,0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 120*1000;
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, (SystemClock.elapsedRealtime()+60*1000), interval, pendingIntent);
    }

}
