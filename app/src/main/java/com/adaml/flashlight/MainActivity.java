package com.adaml.flashlight;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

public class MainActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFlashlightAvailable()) {
            Intent intent = new Intent(this, FlashlightActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "No flashlight available on this device.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean isFlashlightAvailable() {
        FlashlightManager flashlightManager = new FlashlightManager(this);
        return flashlightManager.isFlashlightAvailable();
    }
}
