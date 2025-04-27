package com.example.historygo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.historygo.AwsServices.Cognito;
import com.example.historygo.AwsServices.CognitoManager;
import com.example.historygo.databinding.ActivityRegisterBinding;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    Cognito authentication;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViewComponents();

        CognitoManager.Companion.getInstance(this, new Function1<Cognito, Unit>() {
            public Unit invoke(Cognito cognitoInstance) {
                if (cognitoInstance != null) {
                    // Cognito inicializado, úsalo
                    authentication = cognitoInstance;
                } else {
                    Log.e("MyActivity", "Error: Cognito es null");
                }
                return Unit.INSTANCE;
            }
        });


        binding.LoginBtn.setOnClickListener(v -> {
            //Toast.makeText(MainActivity.this, "This is a Toast message!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, Login.class);
            startActivity(intent);
        });
    }

    private void initViewComponents() {
        binding.SignupBtn.setOnClickListener(view -> {
            String name = binding.Name.getText().toString().trim();
            String email = binding.Email.getText().toString().trim();
            String password = Objects.requireNonNull(binding.Password.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(binding.ConfirmPassword.getText()).toString().trim();


            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                // Muestra un mensaje de error
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            userId = binding.Email.getText().toString().replace(" ", "");
            authentication.addAttribute("family_name", userId);
            authentication.addAttribute("email", binding.Email.getText().toString().replace(" ", ""));
            authentication.signUpInBackground(userId, binding.Password.getText().toString());

            Intent intent = new Intent(RegisterActivity.this, VerifyAcountActivity.class);
            intent.putExtra("userId", userId); // si necesitas pasarlo
            startActivity(intent);
        });
    }
}