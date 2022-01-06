package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    void LoadInboxMessage() {
        try {
            String sms = "";
            Uri uri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            cursor.moveToPosition(counter);
            String title = cursor.getString((int) cursor.getColumnIndex("address"));
            String body =cursor.getString((int) cursor.getColumnIndex("body"));
            if(cursor.moveToNext()) {
                sms = "From : " + title + "\n" + body + "\n";
                displayNotification(title,body ,counter);
            }
            TextView txt = (TextView) findViewById(R.id.tv);
            txt.setText(sms);

            counter++;
        }catch (Exception e){}

    }


    public void loadsms(View view) {
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.M &&
                 checkSelfPermission(Manifest.permission.READ_SMS)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_SMS},1200);
        }else{
            LoadInboxMessage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LoadInboxMessage();

        } else {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void displayNotification(String title,String body,int id){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_ID = "HEADS_UP_NOTIFICATIONS";
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MyNotification",
                    NotificationManager.IMPORTANCE_HIGH);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.phone)
                    .setStyle(new Notification.BigTextStyle())
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);
            NotificationManagerCompat.from(this).notify(id, notification.build());

        }else{
            ////////////////////////////////////////////////////////////////////////////////
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            ///////////////////////////////////////////////////////////////////////////////
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.phone)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText((CharSequence) body))
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id, notificationBuilder.build());

        }
    }
}



