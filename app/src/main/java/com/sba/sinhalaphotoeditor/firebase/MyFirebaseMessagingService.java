//package com.sba.sinhalaphotoeditor.firebase;
//
//import android.app.LauncherActivity;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.sba.sinhalaphotoeditor.MainActivity;
//import com.sba.sinhalaphotoeditor.R;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService
//{
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
//    {
//        super.onMessageReceived(remoteMessage);
//
//        if(remoteMessage.getNotification() != null)
//        {
//            if(remoteMessage.getNotification().getTitle() != null && remoteMessage.getNotification().getBody() != null)
//            {
//                String message =  remoteMessage.getNotification().getBody();
//                String title = remoteMessage.getNotification().getTitle();
//
//
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                int notifyId = 1;
//                String channelId = "some_channel_id";
//
//                Notification notification = new Notification.Builder(MyFirebaseMessagingService.this)
//                        .setContentTitle(title)
//                        .setContentText(message)
//                        .setSmallIcon(R.drawable.greenlogo)
//                        .build();
//
//                notificationManager.notify(notifyId,notification);
//                //sendNotification(MyFirebaseMessagingService.this,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//            }
//        }
//
//    }
//
//    @Override
//    public void onNewToken(@NonNull String s)
//    {
//        Log.d("token",s);
//        super.onNewToken(s);
//    }
//    public void sendNotification(Context context,String title,String message)
//    {
//        String idChannel = "my_channel_01";
//        Intent mainIntent;
//
//        mainIntent = new Intent(context, MainActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationChannel mChannel = null;
//        // The id of the channel.
//
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, null);
//        builder.setContentTitle(context.getString(R.string.app_name))
//                .setSmallIcon(R.drawable.greenlogo)
//                .setContentIntent(pendingIntent)
//                .setContentTitle(title)
//                .setContentText(message);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            mChannel = new NotificationChannel(idChannel, context.getString(R.string.app_name), importance);
//            // Configure the notification channel.
//            mChannel.setDescription(message);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//            if(mNotificationManager != null)
//            {
//                mNotificationManager.createNotificationChannel(mChannel);
//            }
//
//        }
//        else
//        {
//            builder.setContentTitle(context.getString(R.string.app_name))
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setColor(ContextCompat.getColor(context,R.color.material_dark_blue))
//                    .setVibrate(new long[]{100, 250})
//                    .setLights(Color.YELLOW, 500, 5000)
//                    .setAutoCancel(true);
//        }
//        if(mNotificationManager != null)
//        {
//            mNotificationManager.notify(1, builder.build());
//        }
//
//    }
//}
