package com.zybooks.finalvoicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import java.io.File;

public class AudioFilesActivity extends AppCompatActivity implements AudioFilesFragment.onItemListClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_files);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.audio_files_fragment_container);

        if (fragment == null) {
            fragment = new AudioFilesFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.audio_files_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onFileSelected(File file) {
        Intent intent = new Intent(this, PlayAudioActivity.class);
        intent.putExtra(PlayAudioActivity.EXTRA_FILE, file.getName());
        startActivity(intent);
    }

}