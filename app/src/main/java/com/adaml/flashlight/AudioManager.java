package com.adaml.flashlight;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioManager {
    private final Context context; // Hold the context to recreate MediaPlayer
    private MediaPlayer mediaPlayer;

    public AudioManager(Context context) {
        this.context = context;
        createMediaPlayer();
    }

    private void createMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release current MediaPlayer
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.flashlight_on);
        mediaPlayer.setLooping(true);
    }

    public void playAudio() {
        if (mediaPlayer == null) {
            createMediaPlayer(); // Create the MediaPlayer if it's not created
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Start the media player
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Stop media player
            createMediaPlayer(); // Recreate the MediaPlayer for future use
        }
    }
}
