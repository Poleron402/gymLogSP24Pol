package com.example.gymlogsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.gymlogsp.Database.GymLogRepo;
import com.example.gymlogsp.Database.entities.GymLog;
import com.example.gymlogsp.Database.entities.User;
import com.example.gymlogsp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_USER_ID = "com.example.gymlogsp";
    static final String SHARED_PREFERENCES_USERID = "com.example.gymlogspSHARED_PREFERENCES_USERID";
    static final String SHARED_PREFERENCES_USERID_VALUE = "com.example.gymlogspSHARED_PREFERENCES_USERID_VALUE";

    ActivityMainBinding binding;
    public static final String TAG = "POLEJIA";
    String mExercise = "";
    double mWeight = 0.0;
    int mReps = 0;
    int loggedInUser = -1;
    private GymLogRepo repo;
    private static final int LOGGEDOUT = -1;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginUser();
        if(loggedInUser == -1){
            Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(intent);
        }

        repo = GymLogRepo.getRepository(getApplication());
        //making the thang scrollable
        binding.logDisplayTextView.setMovementMethod(new ScrollingMovementMethod());
        upDateDisplay();
        //hooking up a button
        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //no toast
                getInformationFromDisplay();
                insertGymLogRecord();
                upDateDisplay();
            }
        });
        binding.exerciseInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDateDisplay();
            }
        });
    }
    private void loginUser(){
        //sharedpreferences

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_USERID, Context.MODE_PRIVATE);
        loggedInUser = sharedPreferences.getInt(SHARED_PREFERENCES_USERID_VALUE, LOGGEDOUT);
        if(loggedInUser == LOGGEDOUT){
            return;
        }
        loggedInUser = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGEDOUT);
        if(loggedInUser == LOGGEDOUT){
            return;
        }
        LiveData<User> userObserver = repo.getUserById(loggedInUser);
        userObserver.observe(this, user -> {
            if (user != null) {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void logout(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_USERID, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(SHARED_PREFERENCES_USERID, LOGGEDOUT);
        sharedPrefEditor.apply();

        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGEDOUT);

        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        if(user == null){
            return false;
        }
        item.setTitle(user.getUsername());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                showAlertDialog();
                logout();
                return false;
            }
        });
        return true;
    }
    private void showAlertDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setTitle("Logout?");
        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout();
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertBuilder.create().show();

    }

    static Intent mainActivityIntentFactory(Context context, int userId){
        Intent intent =  new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID,  userId);
        return intent;
    }
    private void insertGymLogRecord(){
        if(mExercise.isEmpty()){
            return;
        }
        GymLog log = new GymLog(mExercise, mWeight, mReps, loggedInUser);
        repo.insertGymLog(log);
    }
    private void upDateDisplay(){
        ArrayList<GymLog> allLogs = repo.getAllLogs();
        if(allLogs.isEmpty()){
            binding.logDisplayTextView.setText("Nothing to show, time to hit the gym");
        }
        StringBuilder sb = new StringBuilder();
        for(GymLog log : allLogs){
            sb.append(log);
        }
        binding.logDisplayTextView.setText(sb.toString());

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