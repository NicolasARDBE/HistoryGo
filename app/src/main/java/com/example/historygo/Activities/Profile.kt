package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.AwsServices.OnAttributesReceivedCallback
import com.example.historygo.R
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.Services.NotificationService
import com.example.historygo.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val jwtDecoder: JWTDecoder = JWTDecoder()
    private lateinit var cognito: Cognito

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance

                // Aquí se piden los atributos una vez que Cognito está listo
                fetchUserAttributes()
            } else {
                Log.e("Profile", "Error: Cognito no disponible")
            }
        }

        binding.changePasswordBtn.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.signOutBtn.setOnClickListener {
            signOut()
            val i = Intent(this, Login::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        binding.updateProfileBtn.setOnClickListener {
            cognito.updateUserAttributes(binding.name.text.toString())
        }
    }

    private fun fetchUserAttributes() {
        cognito.getUserAttributes(object : OnAttributesReceivedCallback {
            override fun onReceived(attributes: Map<String, String>) {
                val name = attributes["family_name"] ?: ""
                val email = attributes["email"] ?: ""

                binding.name.setText(name)
                binding.email.setText(email)
            }

            override fun onError(exception: Exception) {
                Log.e("Profile", "Error al obtener atributos: ${exception.message}")
            }
        })
    }

    private fun signOut() {
        cognito.cognitoCachingCredentialsProvider.clear()

        val serviceIntent = Intent(this, NotificationService::class.java)
        stopService(serviceIntent)

        val sharedPreferences = getSharedPreferences("mi_app_pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val sharedPreferencesJwt = getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPreferencesJwt.edit().remove("jwt_token").apply()

        cognito.UserSignOut()
    }
}
