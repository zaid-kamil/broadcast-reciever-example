package com.example.broadcastrecieverexample;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.broadcastrecieverexample.MainActivity.NOTIFICATION_ID;
import static com.example.broadcastrecieverexample.MainActivity.PRIMARY_CHANNEL_ID;
import static com.example.broadcastrecieverexample.MainActivity.SECONDARY_CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class CustomReceiver extends BroadcastReceiver {
    private NotificationManager mNotifyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        String intentAction = intent.getAction();
        if (intentAction != null) {
            String toastMessage = "unknown intent action";
            switch (intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    toastMessage = "Power connected!";
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    toastMessage = "Power disconnected!";
                    break;
                case MainActivity.ACTION_CUSTOM_BROADCAST:
                    toastMessage = "Our customized broadcast";
                    break;
            }
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            sendNotification(context,toastMessage);
        }
    }

    public void createNotificationChannel(Context context) {
        mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    SECONDARY_CHANNEL_ID,
                    "Broadcast Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Broadcast");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                notificationChannel.setAllowBubbles(true);
            }
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }


    private void sendNotification(Context context, String message) {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(context,message);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, String message) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, SECONDARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        return notifyBuilder;
    }
}