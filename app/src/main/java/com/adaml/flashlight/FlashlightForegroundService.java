package com.adaml.flashlight;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class FlashlightForegroundService extends Service {
    private static final String CHANNEL_ID = "flashlight_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = buildNotification();
        startForeground(1, notification);
        toggleFlashlight(); // Assuming you handle flashlight toggle here
        return START_STICKY;
    }

    private void createNotificationChannel() {
        CharSequence name = "Flashlight Service Channel";
        String description = "Notification channel for flashlight service";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Flashlight")
                .setContentText("Using the flashlight")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    private void toggleFlashlight() {
        // Toggle the flashlight on or off
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // No binding provided
    }
}
