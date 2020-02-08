package com.sba.sinhalaphotoeditor.firebase;

import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.sba.sinhalaphotoeditor.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {

        if(remoteMessage != null && remoteMessage.getNotification() != null)
        {
            String message = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();

            if (message != null && title != null && !message.equals("") && !title.equals(""))
            {
                createNotification(title, message);
            }

        }
    }

    @Override
    public void onNewToken(@NonNull String s)
    {
        //saveToken(s);
    }
    public void createNotification(String title, String message)
    {



        //RemoteViews collapsedView = new RemoteViews(this.getPackageName(),R.layout.notication_collapsed );
        //collapsedView.setTextViewText(R.id.title,title);

        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(this , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.greenlogo);
        mBuilder.setAutoCancel(false)
               .setContentTitle(title)
                .setContentText(message)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        if(mNotificationManager != null)
        {
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
        }

    }
    public void saveToken(String token)
    {
        SharedPreferences preferences = this.getSharedPreferences(getPackageName(),0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Token",token);
        editor.apply();
    }
    public String getToken()
    {
        SharedPreferences preferences = this.getSharedPreferences(getPackageName(),0);
        return  preferences.getString("Token",null);
    }

}
