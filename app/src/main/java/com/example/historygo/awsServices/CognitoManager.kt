package com.example.historygo.awsServices

import android.content.Context
import android.util.Log
import android.widget.Toast

class CognitoManager private constructor(context: Context) {
    private var cognito: Cognito? = null



    init {
        cognito = Cognito(context, object : Cognito.OnInitializedCallback {
            override fun onInitialized(cognitoInstance: Cognito) {
                // Cognito está completamente inicializado, ahora podemos continuar con la lógica de la actividad
                Log.d("Cognito", "Cognito inicializado correctamente")
            }

            override fun onError(e: Exception) {
                // Si hay un error durante la inicialización de Cognito, mostramos un mensaje de error
                Toast.makeText(context,"Error al inicializar Cognito: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @Volatile
        private var INSTANCE: CognitoManager? = null

        fun getInstance(context: Context): CognitoManager {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: CognitoManager(context)
                INSTANCE = instance
                instance
            }
        }
    }

    // Método para obtener la instancia de Cognito
    fun getCognito(): Cognito? {
        return cognito
    }
}