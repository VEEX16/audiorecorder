package com.example.dyktafon;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_CODE = 21;
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView fileNameText;
    private EditText filename;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;

    private boolean isRecording = false;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_button);
        timer = view.findViewById(R.id.record_timer);
        fileNameText = view.findViewById(R.id.record_filename);
        filename = view.findViewById(R.id.filename);

        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.record_list_btn:
                if(isRecording){
                }else{
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }

                break;

            case R.id.record_button:
                if(isRecording){
                    //Stop recording
                    stopRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_stop));
                    isRecording = false;
                } else{
                    //Start recording
                    if(checkPermissions()){
                        startRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_start));
                    isRecording = true;
                    }
                }

                break;


        }
    }

    private void startRecording() {
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        if(filename.getText().toString() == "Filename") {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH);
            Date now = new Date();
            recordFile = formatter.format(now) + ".3gp";

        } else {
            recordFile = filename.getText().toString() + ".3gp";
        }

        fileNameText.setText("Recording...");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        mediaRecorder.start();
    }

    private void stopRecording() {
        timer.stop();
        fileNameText.setText("Recording stoped. File saved");
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

    }

        private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording) {
            stopRecording();
        }
    }
}
