package com.example.gymlogsp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import com.example.gymlogsp.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final String TAG = "POLEJIA";
    String mExercise = "";
    double mWeight = 0.0;
    int mReps = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //making the thang scrollable
        binding.logDisplayTextView.setMovementMethod(new ScrollingMovementMethod());

        //hooking up a button
        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //no toast
                getInformationFromDisplay();
                upDateDisplay();
            }
        });
    }
    private void upDateDisplay(){
        String currentInfo = binding.logDisplayTextView.getText().toString();
        String newDisplay = String.format(Locale.US, "Exercise:%s\nWeight:%.2f\nReps:%d\n=====================\n", mExercise, mWeight, mReps);
        currentInfo+=newDisplay;
        binding.logDisplayTextView.setText(currentInfo);

    }
    private void getInformationFromDisplay(){
        mExercise = binding.exerciseInputEditText.getText().toString();
        try {
            mWeight = Double.parseDouble(binding.weightInputEditText.getText().toString());
        }catch(NumberFormatException e){
            //This writes better info to logcat
            Log.d(TAG, "Error reading value from weight.");
        }
        try {
            mReps = Integer.parseInt(binding.repsInputEditText.getText().toString());
        }catch(NumberFormatException e){
            Log.d(TAG, "Error reading value from reps.");
        }
    }
}