package com.zybooks.finalvoicerecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayAudioActivity extends AppCompatActivity {

    public static String EXTRA_FILE = "1";
    private File[] allFiles;
    private File audioFile = null;
    private TextView fileNameText;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private boolean isPlaying = false;
    private int mCurrentFile = 0;
    private EditText mRenameFile;
    private Button mRenameButton;
    private MediaRecorder mediaRecorder;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        path = getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();

        int holder = getIntent().getIntExtra(EXTRA_FILE, 1);
        mCurrentFile = holder;

        audioFile = allFiles[holder];

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu for the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_audio_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Determine which app bar item was chosen
        switch (item.getItemId()) {

            case R.id.delete:
                DeleteFile();
                return true;

            case R.id.rename:
                RenameFile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DeleteFile() {
        List<File> fileList = new ArrayList<>(Arrays.asList(allFiles));
        fileList.remove(mCurrentFile);
        audioFile.delete();
        mCurrentFile --;
        allFiles = fileList.toArray(new File[allFiles.length -1]);
        updateFileInfo();
    }

    private void updateFileInfo() {
        fileNameText.setText(allFiles[mCurrentFile].getName());
        audioFile = allFiles[mCurrentFile];
    }

    private void RenameFile() {
        mRenameFile = findViewById(R.id.renameText);
        mRenameButton = findViewById(R.id.renameButton);
        mRenameButton.setVisibility(View.VISIBLE);
        mRenameFile.setVisibility(View.VISIBLE);
    }

    public void playClick(View view) {
        playAudio(audioFile);
    }

    public void prevClick(View view) {
        if (isPlaying == true){
            stopAudio();
        }
        if ((mCurrentFile - 1) < 0 ){
            mCurrentFile = allFiles.length - 1;
            fileNameText.setText(getFileName(mCurrentFile));
            setFile(mCurrentFile);
        }
        else{
            mCurrentFile --;
            fileNameText.setText(getFileName(mCurrentFile));
            setFile(mCurrentFile);
        }
    }

    public void nextClick(View view) {
        if ((mCurrentFile + 1) == allFiles.length){
            mCurrentFile = 0;
            fileNameText.setText(getFileName(mCurrentFile));
            setFile(mCurrentFile);
        }
        else{
            mCurrentFile ++;
            fileNameText.setText(getFileName(mCurrentFile));
            setFile(mCurrentFile);
        }
    }

    public void setFile(int i){
        audioFile = allFiles[i];
    }

    public String getFileName(int i){

        return allFiles[i].getName();
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
        mediaPlayer.stop();
        isPlaying = false;
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.play, null));
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

    public void renameClick(View view) {

        audioFile.setReadable(true);
        audioFile.setWritable(true);

        if (mRenameFile.getText() != null && String.valueOf(mRenameFile.getText()) != ""){
            String recordPath = getExternalFilesDir("/").getAbsolutePath();
            String newFileName = mRenameFile.getText() + ".3gp";

            String path = recordPath + "/" + newFileName;
            File newFile = new File(recordPath + "/" + newFileName);
            newFile.setWritable(true);
            newFile.setReadable(true);

            if(audioFile.renameTo(newFile)){

                path = getExternalFilesDir("/").getAbsolutePath();
                File directory = new File(path);
                allFiles = directory.listFiles();
                audioFile = allFiles[mCurrentFile];

                fileNameText.setText(audioFile.getName());

                mRenameFile.setVisibility(View.INVISIBLE);
                mRenameButton.setVisibility(View.INVISIBLE);
            }
            else{
                mRenameFile.setText("Error Changing File Name");
            }
        }
        else{
            mRenameFile.setText("Error Changing File Name");
        }
    }
}