package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.R
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.Services.NotificationService
import com.example.historygo.databinding.ActivityProfileBinding
import kotlin.math.sign

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
            } else {
                Log.e("MiActivity", "Error: Cognito no disponible")
            }
        }

        binding.name.setText(jwtDecoder.decodeJWTCognitoFamilyName(jwtToken))
        binding.email.setText(jwtDecoder.decodeJWTCognitoEmail(jwtToken))

        binding.changePasswordBtn.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.signOutBtn.setOnClickListener{
            signOut()
            val i = Intent(this, Login::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }



    }

    private fun signOut(){
        //Cierra sesión localmente
        cognito.cognitoCachingCredentialsProvider.clear()
        val serviceIntent = Intent(this, NotificationService::class.java)
        stopService(serviceIntent)

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