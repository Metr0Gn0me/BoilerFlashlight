package com.adaml.flashlight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.ComponentActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class FlashlightActivity extends ComponentActivity {
    private final Handler timerHandler = new Handler();
    private FlashlightManager flashlightManager;
    private MaterialButton toggleButton;
    private TextView timerTextView;
    private AudioManager audioManager;
    private long startTime = 0;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format(Locale.US, "Time: %02d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toggle_button);
        flashlightManager = new FlashlightManager(this);
        audioManager = new AudioManager(this);
        timerTextView = findViewById(R.id.timerTextView);
        toggleButton = findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(v -> {
            boolean isChecked = toggleButton.isChecked();  // Get current state
            handleToggleButtonChange(isChecked);
        });
    }

    private void handleToggleButtonChange(boolean isChecked) {
        int batteryLevel = getBatteryLevel();
        if (isChecked) {
            if (false) { //batteryLevel > 20
                toggleButton.setText(getResources().getString(R.string.flash_on));
                flashlightOnOperations();
            } else {
                showLowBatteryDialog(batteryLevel);  // This will handle the operations based on user input
            }
        } else {
            toggleButton.setText(getResources().getString(R.string.flash_off));
            flashlightOffOperations();
        }
    }

    private void flashlightOnOperations() {
        flashlightManager.toggleFlashlight(true);
        startTime = System.currentTimeMillis();
        audioManager.playAudio();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @SuppressLint("SetTextI18n")
    private void flashlightOffOperations() {
        flashlightManager.toggleFlashlight(false);
        timerHandler.removeCallbacks(timerRunnable);
        audioManager.stopAudio();
        startTime = 0; // Reset the timer
        timerTextView.setText("Time: 00:00:00");
    }


    private void showLowBatteryDialog(int batteryLevel) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Battery Low")
                .setMessage("Battery is at " + batteryLevel + "%, would you still like to turn on the flash?")
                .setPositiveButton("Yes", (dialogInterface, which) -> {
                    toggleButton.setChecked(true);
                    toggleButton.setText(getResources().getString(R.string.flash_on));
                    flashlightOnOperations();
                })
                .setNegativeButton("No", (dialogInterface, which) -> {
                    toggleButton.setChecked(false);
                    toggleButton.setText(getResources().getString(R.string.flash_off));
                })
                .show();
    }

    private int getBatteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, filter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        return (int) ((level / (float) scale) * 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toggleButton.isChecked()) {
            startTime = System.currentTimeMillis() - (System.currentTimeMillis() - startTime);
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }
}