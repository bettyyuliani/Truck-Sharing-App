package com.example.task82;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.task82.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ChatMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // get the logged in user's username from shared preferences and assign it to the respective variable
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        String loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");
        Util.updateDeviceToken(this, s,loggedInUsername );
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        // fetch data from the notification received
        String title = remoteMessage.getData().get(Util.NOTIFICATION_TITLE);
        String message = remoteMessage.getData().get(Util.NOTIFICATION_MESSAGE);

        // get the details of the logged in user from shared preferences
        SharedPreferences prefs = getSharedPreferences(Util.SHARED_PREF_DATA, MODE_PRIVATE);
        String loggedInUsername = prefs.getString(Util.LOGGEDIN_USER, "");

        Intent chatIntent; // create a new intent object

        // assign intents depending on the logged in user
        if (loggedInUsername.equals("driver")) chatIntent = new Intent(this, DriverChatsActivity.class);
        else chatIntent = new Intent(this, MessageActivity.class);

        // wrap chatIntent in pendingIntent as it can only be used once
        PendingIntent pendingIntent  = PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_ONE_SHOT);

        // create Notification Manager to show the notification
        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // create default ringtone
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // default ringtone

        // create new Notification Builder
        final NotificationCompat.Builder notificationBuilder;

        // create Notification Channel to group notifications separately
        NotificationChannel channel = new NotificationChannel(Util.CHANNEL_ID, Util.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(Util.CHANNEL_DESCRIPTION); // set description of channel
        notificationManager.createNotificationChannel(channel); // bind channel to notification manager
        notificationBuilder = new NotificationCompat.Builder(this,Util.CHANNEL_ID); // build a new notification with the channel id

        // binding data to notification builder
        notificationBuilder.setSmallIcon(R.drawable.head); // to show small icon
        notificationBuilder.setColor(getResources().getColor(R.color.black)); // set text of notification
        notificationBuilder.setContentTitle(title); // set title of notification
        notificationBuilder.setAutoCancel(true); // allow auto cancel
        notificationBuilder.setSound(defaultSoundUri); // play a sound when notification is received
        notificationBuilder.setContentIntent(pendingIntent); // set intent when notification is clicked
        notificationBuilder.setContentText(message); // set text of the notification
        notificationManager.notify(999, notificationBuilder.build()); // build notification
    }
}