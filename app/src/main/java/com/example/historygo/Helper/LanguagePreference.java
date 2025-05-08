package com.example.historygo.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguagePreference {
    private static final String KEY_LANGUAGE = "language";
    private static final String PREF_NAME = "app_prefs";

    public static void saveLanguage(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "es"); // idioma por defecto
    }
}