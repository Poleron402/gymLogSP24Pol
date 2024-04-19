package com.example.gymlogsp;
import com.example.gymlogsp.Database.entities.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.example.gymlogsp.Database.GymLogRepo;
import com.example.gymlogsp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    //silly lil comment
    private ActivityLoginBinding binding;
    private GymLogRepo repo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repo = GymLogRepo.getRepository(getApplication());
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyUser();
            }
        });
    }
    private void verifyUser(){
        String username = binding.userNameLoginTextView.getText().toString();
        if(username.isEmpty()){
            Toast.makeText(this, "Username may not be bleank", Toast.LENGTH_SHORT).show();
            return;
        }
        LiveData<User> userObserver = repo.getUserByUserName(username);
        userObserver.observe(this, user -> {
            if(user != null){
                String password = binding.passwordLoginTextView.getText().toString();
                if(password.equals(user.getPassword())){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFERENCES_USERID, Context.MODE_PRIVATE);
                    SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
                    sharedPrefEditor.putInt(MainActivity.SHARED_PREFERENCES_USERID, user.getId());
                    sharedPrefEditor.apply();
                    startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), user.getId()));
                }else{
                    toastMaker("Invalid pw");
                    binding.passwordLoginTextView.setSelection(0);
                }
            }else{
                toastMaker(String.format("No user %s found. Not a valid username", username));
                binding.userNameLoginTextView.setSelection(0);
            }
        });

    }

    private void toastMaker(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    static Intent loginIntentFactory(Context context){
        return new Intent(context, LoginActivity.class);
    }
}