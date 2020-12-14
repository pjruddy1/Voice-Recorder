package com.zybooks.finalvoicerecorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class AudioFilesFragment extends Fragment {

    private File[] allFiles;
    private AudioFileAdapter audioFileAdapter;
    private RecyclerView audioFiles;
    private onItemListClick mListener;

    public interface onItemListClick {
        void onFileSelected(int i);
    }

    public AudioFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_files, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.file_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Send files to recycler view
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        for (int i = 0; i < allFiles.length; i ++){
            AudioFileAdapter adapter = new AudioFileAdapter(allFiles[i], i);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
//        File directory = new File(path);
//        allFiles = directory.listFiles();
//
//        audioFileAdapter = new AudioFileAdapter(allFiles, 0);
//        audioFiles = view.findViewById(R.id.file_recycler_view);
//
//        audioFiles.setHasFixedSize(true);
//        audioFiles.setLayoutManager(new LinearLayoutManager(getContext()));
//        audioFiles.setAdapter(audioFileAdapter);
//
//    }

    public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.AudioViewHolder> {

        private File audioFile;
        private int filePostion;
        private String mHolder;

        private Object objectHolder;

        private onItemListClick onItemListClick;

        public AudioFileAdapter(File file, int position) {
            audioFile = file;
            filePostion = position;
        }

        @NonNull
        @Override
        public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_file_item, parent, false);
            //view.setTag(mHolder);

            return new AudioViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
            holder.file_title.setText(allFiles[position].getName());
            holder.file_title.setTag(position);
        }

        @Override
        public int getItemCount() {
            return allFiles.length;
        }

        public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView file_image;
            private TextView file_title;

            public AudioViewHolder(@NonNull View itemView, int i) {
                super(itemView);
                itemView.setOnClickListener(this);
                //mHolder = allFiles[i].getName();
                itemView.setTag(i);
                objectHolder = itemView.getTag();
                file_image = itemView.findViewById(R.id.file_image_view);
                file_title = itemView.findViewById(R.id.file_title);

            }

            @Override
            public void onClick(View v) {
                mListener.onFileSelected((int) v.findViewById(R.id.file_title).getTag());
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onItemListClick) {
            mListener = (onItemListClick) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}