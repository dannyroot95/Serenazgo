package com.example.serenazgo.Canales;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.serenazgo.R;
public class NotificationHelper extends ContextWrapper {

    static final String CHANNEL_ID = "com.example.serenazgo";
    static final String CHANNEL_NAME = "serenazgo";
    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        createchannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createchannel(){

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH

        );

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);

    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title , String body , PendingIntent intent , Uri sounduri){

        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sounduri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setStyle(new Notification.BigTextStyle().bigText(body).setBigContentTitle(title));

    }


    public NotificationCompat.Builder getNotificationOlApi(String title , String body , PendingIntent intent , Uri sounduri){

        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sounduri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));

    }

}
