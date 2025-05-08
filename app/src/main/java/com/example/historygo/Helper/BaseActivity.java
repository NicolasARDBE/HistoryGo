package com.example.historygo.Helper;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // Aplica el idioma guardado al contexto
        String language = LanguagePreference.getLanguage(newBase); // "es" o "en"
        Context contextWithLocale = LocaleHelper.setLocale(newBase, language);
        super.attachBaseContext(contextWithLocale);
    }
}