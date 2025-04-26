package com.example.historygo.AwsServices

import android.content.Context
import android.util.Log
import android.widget.Toast

// Singleton
class CognitoManager private constructor(context: Context) {
    private var cognito: Cognito? = null

    init {
        cognito = Cognito(context, object : Cognito.OnInitializedCallback {
            override fun onInitialized(cognitoInstance: Cognito) {
                // Cognito completamente inicializado
                Log.d("Cognito", "Cognito inicializado correctamente")
                cognito = cognitoInstance
            }

            override fun onError(e: Exception) {
                Toast.makeText(context, "Error al inicializar Cognito: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @Volatile
        private var INSTANCE: CognitoManager? = null

        fun getInstance(context: Context, onReady: (Cognito?) -> Unit) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = CognitoManager(context)
                    }
                }
            }
            // Retorna la instancia de Cognito (puede ser null si no se inicializó todavía)
            onReady(INSTANCE?.cognito)
        }
    }
}
