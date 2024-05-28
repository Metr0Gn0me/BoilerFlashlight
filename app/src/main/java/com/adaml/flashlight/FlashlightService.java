package com.adaml.flashlight;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Locale;

public class FlashlightService extends Service {
    public static final String ACTION_TOGGLE_FLASHLIGHT = "TOGGLE_FLASHLIGHT";
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private boolean isFlashlightOn = false;
    private long startTime;
    private MediaPlayer mediaPlayer;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    public void onCreate() {
        super.onCreate();
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = getCameraId();
        } catch (CameraAccessException e) {
            Log.e("FlashlightService", "Camera access exception", e);
        }
    }

    private String getCameraId() throws CameraAccessException {
        for (String id : cameraManager.getCameraIdList()) {
            Boolean flashAvailable = cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (flashAvailable != null && flashAvailable) {
                return id;
            }
        }
        return null; // return null if no camera with flash is found
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "TOGGLE_FLASHLIGHT".equals(intent.getAction())) {
            toggleFlashlight(!isFlashlightOn); // Toggle the flashlight based on its current state
        }
        return START_STICKY;
    }

    private void toggleFlashlight(boolean enable) {
        if (enable != isFlashlightOn) {
            isFlashlightOn = enable;
            updateFlashlight(enable);
            updateMediaPlayer(enable);
            updateTimer();
            updateWidget();
        }
    }

    private void updateFlashlight(boolean enable) {
        try {
            cameraManager.setTorchMode(cameraId, enable);
        } catch (CameraAccessException e) {
            Log.e("FlashlightService", "Failed to set torch mode", e);
        }
    }

    private void updateMediaPlayer(boolean enable) {
        if (enable) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.flashlight_on);
                mediaPlayer.setLooping(true);
            }
            mediaPlayer.start();
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private void updateTimer() {
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NewAppWidget.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.timer_text, formatTimer());
            views.setTextViewText(R.id.toggle_flashlight, isFlashlightOn ? "Flash OFF" : "Flash ON");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private String formatTimer() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }    private final Runnable timerRunnable = () -> {
        if (!isFlashlightOn) {
            return;
        }

        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        String timerText = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.timer_text, timerText);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, NewAppWidget.class);
        appWidgetManager.updateAppWidget(thisWidget, views);

        updateTimer();
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




}
