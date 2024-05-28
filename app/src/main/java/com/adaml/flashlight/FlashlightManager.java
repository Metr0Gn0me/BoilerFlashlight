package com.adaml.flashlight;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

public class FlashlightManager {
    private final CameraManager cameraManager;
    private String cameraId;

    public FlashlightManager(Context context) {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String id : cameraManager.getCameraIdList()) {
                if (Boolean.TRUE.equals(cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE))) {
                    cameraId = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Log.e("FlashlightManager", "Failed to access camera for flashlight", e);
        }
    }

    public boolean isFlashlightAvailable() {
        return cameraId != null;
    }

    public void toggleFlashlight(boolean enable) {
        if (cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId, enable);
            } catch (CameraAccessException e) {
                Log.e("FlashlightManager", "Error toggling flashlight", e);
            }
        }
    }
}
