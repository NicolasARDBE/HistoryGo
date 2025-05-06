package com.example.historygo.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.historygo.AwsServices.Cognito;
import com.example.historygo.AwsServices.CognitoManager;
import com.example.historygo.Helper.BaseActivity;
import com.example.historygo.Helper.LanguagePreference;
import com.example.historygo.R;
import com.example.historygo.databinding.ActivityLoginBinding;

import java.util.Objects;

import kotlin.Unit;

public class Login extends BaseActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        requestNotificationPermission();
        String currentLanguage = LanguagePreference.getLanguage(this);
        binding.languageSwitch.setChecked("en".equals(currentLanguage));


        //Switch de idioma
        binding.languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String language = isChecked ? "en" : "es";
            LanguagePreference.saveLanguage(this, language);

            // Reinicia la actividad con nuevo intent limpio
            Intent intent = new Intent(this, getClass());
            finish();
            startActivity(intent);
        });
        binding.RegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        });

        initViewComponents();
    }

    private void initViewComponents() {
        binding.LoginBtn.setOnClickListener(view -> {
            String email = binding.Email.getText().toString().trim();
            String password = Objects.requireNonNull(binding.Password.getText()).toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.fields, Toast.LENGTH_SHORT).show();
            } else {
                CognitoManager.Companion.getInstance(this, cognitoInstance -> {
                    if (cognitoInstance != null) {
                        cognitoInstance.userLogin(email, password);
                    } else {
                        Log.e("Login", "Error: Cognito es null");
                    }
                    return Unit.INSTANCE;
                });
            }
        });

        binding.ForgotPasswordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        binding.VerifyAccountBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, VerifyAcountActivity.class);
            startActivity(intent);
        });
    }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.notification_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.notification_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
