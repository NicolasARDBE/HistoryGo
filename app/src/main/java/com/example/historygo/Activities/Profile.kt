package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.AwsServices.OnAttributesReceivedCallback
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.Services.NotificationService
import com.example.historygo.databinding.ActivityProfileBinding

class Profile : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var cognito: Cognito

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
                // Aquí se piden los atributos una vez que Cognito está listo
                fetchUserAttributes()
            } else {
                Log.e("Profile", "Error: Cognito no disponible")
            }
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
