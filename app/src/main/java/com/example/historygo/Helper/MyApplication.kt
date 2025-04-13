package com.example.historygo.Helper

import android.app.Application
import com.example.historygo.AwsServices.CognitoManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CognitoManager.getInstance(applicationContext)
    }
}