package com.example.historygo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.historygo.awsServices.Cognito;
import com.example.historygo.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViewComponents();

        // Register button click listener to show Toast
        binding.RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViewComponents(){
        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming Cognito class handles login logic
                Cognito authentication = new Cognito(getApplicationContext());
                authentication.userLogin(binding.Email.getText().toString().replace(" ", ""), binding.Password.getText().toString());
            }
        });
    }
}
