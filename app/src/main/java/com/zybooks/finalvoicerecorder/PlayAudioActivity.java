package com.zybooks.finalvoicerecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class PlayAudioActivity extends AppCompatActivity {

    public static String EXTRA_FILE = null;
    private File[] allFiles;
    private File audioFile = null;
    private TextView fileNameText;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        String path = getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();

        for (File file: allFiles){
            if (file.getName() == EXTRA_FILE){
                audioFile = file;
            }
        }

        playButton = findViewById(R.id.playButton);
        fileNameText = findViewById(R.id.fileName);
        playerSeekbar = findViewById(R.id.playerSeekbar);
        fileNameText.setText(audioFile.getName());

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

    }

    public void playClick(View view) {
        playAudio(audioFile);
    }

    public void prevClick(View view) {
    }

    public void nextClick(View view) {
    }

    private void playAudio(File file) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playButton.setImageDrawable(getResources().getDrawable(R.drawable.stop, null));

        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        getUpdateSeekBar();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void stopAudio() {
        //Stop The Audio
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.play, null));
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);

    }

    private void pauseAudio() {
        mediaPlayer.pause();
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.play, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.stop, null));
        isPlaying = true;

        getUpdateSeekBar();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void getUpdateSeekBar() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }
}