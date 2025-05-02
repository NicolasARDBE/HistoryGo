package com.example.historygo.Helper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager

class MyApplication : Application() {

    private var activityCount = 0
    private lateinit var cognito: Cognito

    override fun onCreate() {
        super.onCreate()

        CognitoManager.getInstance(applicationContext) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
                setupLifecycleCallbacks()
            } else {
                Log.e("MyApplication", "Error: Cognito no se inicializó correctamente")
            }
        }
    }

    private fun setupLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                activityCount++
            }

            override fun onActivityStopped(activity: Activity) {
                activityCount--
                if (activityCount == 0) {
                    // App en background o cerrada
                    Log.d("MyApplication", "App en background, cerrando sesión...")

                    cognito.cognitoCachingCredentialsProvider.clear()

                    val sharedPreferences = getSharedPreferences("mi_app_pref", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()

                    val sharedPreferencesJwt = getSharedPreferences("auth", Context.MODE_PRIVATE)
                    sharedPreferencesJwt.edit().remove("jwt_token").apply()

                    cognito.UserSignOut()
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}