package com.example.historygo.Helper

import android.app.Application
import com.example.historygo.awsServices.CognitoManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CognitoManager.getInstance(applicationContext)

    }
}