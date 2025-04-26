package com.example.historygo.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.historygo.AwsServices.Cognito;
import com.example.historygo.databinding.ActivityLoginBinding;
import com.example.historygo.AwsServices.CognitoManager;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Login extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Set up Strict Mode
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViewComponents();

        //Request notification permission if Android 13+
        requestNotificationPermission();

        binding.RegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initViewComponents() {
        binding.LoginBtn.setOnClickListener(view -> {
            String email = binding.Email.getText().toString().trim();
            String password = binding.Password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                CognitoManager.Companion.getInstance(this, new Function1<Cognito, Unit>() {
                    public Unit invoke(Cognito cognitoInstance) {
                        if (cognitoInstance != null) {
                            // Cognito inicializado, úsalo
                            cognitoInstance.userLogin(email, password);
                        } else {
                            Log.e("MyActivity", "Error: Cognito es null");
                        }
                        return Unit.INSTANCE;
                    }
                });
            }
        });

        binding.ForgotPasswordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    // Function to request POST_NOTIFICATIONS permission
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
