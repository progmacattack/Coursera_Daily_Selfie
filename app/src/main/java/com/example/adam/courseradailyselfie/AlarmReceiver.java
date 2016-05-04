package com.example.adam.courseradailyselfie;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

/**
 * Created by Adam on 5/1/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private int notificationID = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"It's time for your selfie!", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_tag_faces_24dp);
        mBuilder.setContentTitle("Your Daily Selfie");
        mBuilder.setContentText("It's time to take your selfie today!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, SelfieEveryDay.class);

        // Adds the back stack for the Intent (but not the Intent itself)
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SelfieEveryDay.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
       mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
