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
        CognitoManager.getInstance(applicationContext)
        cognito = CognitoManager.getInstance(applicationContext).getCognito()!!


        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                activityCount++
            }

            override fun onActivityStopped(activity: Activity) {
                activityCount--
                if (activityCount == 0) {
                    // App ya no tiene activities activas -> background o cerrada
                    Log.d("MyApplication", "App en background, cerrando sesión...")

                    //Cierra sesión localmente
                    cognito.cognitoCachingCredentialsProvider.clear()
                    val sharedPreferences = getSharedPreferences("mi_app_pref", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    val sharedPreferencesJwt = getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val editorJwt = sharedPreferencesJwt.edit()
                    editorJwt.remove("jwt_token")
                    editorJwt.apply()

                    cognito.UserSignOut()
                }
            }

            // Métodos no usados (pueden dejarse vacíos)
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
