package com.sba.sinhalaphotoeditor.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sba.sinhalaphotoeditor.BuildConfig;
import com.sba.sinhalaphotoeditor.config.Constants;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.activities.NotificationView;

import static com.sba.sinhalaphotoeditor.config.Constants.FIREBASE_PAYLOAD_MESSAGE_TEXT;
import static com.sba.sinhalaphotoeditor.config.Constants.FIREBASE_PAYLOAD_TITLE_TEXT;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {

        if(remoteMessage.getNotification() != null)
        {
            String message = remoteMessage.getData().get(FIREBASE_PAYLOAD_MESSAGE_TEXT);
            String title = remoteMessage.getData().get(FIREBASE_PAYLOAD_TITLE_TEXT);

            if (message != null && title != null && !message.equals("") && !title.equals(""))
            {
                createNotification(title, message);
            }


        }
    }

    @Override
    public void onNewToken(@NonNull String s)
    {
        if(BuildConfig.DEBUG)
        {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_DEBUG_PROMOTION_TOPIC_NAME).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_DEBUG_UPDATE_AVAILABLE_TOPIC_NAME);
                }
            });


        }
        else
        {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_RELEASE_PROMOTION_TOPIC_NAME).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_RELEASE_UPDATE_AVAILABLE_TOPIC_NAME);
                }
            });
        }

        //saveToken(s);
    }
    public void createNotification(String title, String message)
    {

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.greenlogo);


        Intent resultIntent = new Intent(this , NotificationView.class);
        resultIntent.putExtra(FIREBASE_PAYLOAD_TITLE_TEXT,title);
        resultIntent.putExtra(FIREBASE_PAYLOAD_MESSAGE_TEXT,message);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.greenlogo);
        mBuilder.setAutoCancel(false)
               .setContentTitle(title)
                .setLargeIcon(bitmap)
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
