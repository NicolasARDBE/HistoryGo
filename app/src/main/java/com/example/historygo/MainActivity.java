package com.example.historygo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.historygo.awsServices.Cognito;
import com.example.historygo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    Cognito authentication;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViewComponents();
        authentication = new Cognito(getApplicationContext());

        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "This is a Toast message!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void initViewComponents(){
        binding.SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    userId = binding.Email.getText().toString().replace(" ", "");
                    authentication.addAttribute("family_name", userId);
                    authentication.addAttribute("email", binding.Email.getText().toString().replace(" ", ""));
                    authentication.signUpInBackground(userId, binding.Password.getText().toString());
            }
        });

        binding.VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authentication.confirmUser(userId, binding.ConfirmationCode.getText().toString().replace(" ", ""));
                //finish();
            }
        });
    }
}