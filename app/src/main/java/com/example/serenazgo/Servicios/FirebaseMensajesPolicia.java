package com.example.serenazgo.Servicios;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.serenazgo.Canales.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMensajesPolicia extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String,String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if(title != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                showNotificationOre(title,body);
            }
            else {
                showNotificacion(title,body);
            }
        }

    }

    private void showNotificacion(String title , String body) {

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOlApi(title,body,intent,sound);
        notificationHelper.getManager().notify(1,builder.build());
    }

    @RequiresApi (api = Build.VERSION_CODES.O)
    private void showNotificationOre(String title , String body) {

        PendingIntent intent = PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotification(title,body,intent,sound);
        notificationHelper.getManager().notify(1,builder.build());

    }
}
