package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var cognito: Cognito
    private val jwtDecoder: JWTDecoder = JWTDecoder()

    override fun onCreate(savedInstanceState: Bundle?) {
        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)

        if(jwtToken != null){
            binding.Correotv.setText(jwtDecoder.decodeJWTCognitoEmail(jwtToken))
        }

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
            } else {
                Log.e("MiActivity", "Error: Cognito no disponible")
            }
        }
        setContentView(binding.root)

        binding.StartProcessBtn.setOnClickListener {
            cognito.forgotPassword(binding.Correotv.text.toString())
        }

        binding.resetPasswordBtn.setOnClickListener {
            cognito.confirmForgotPassword(binding.Codetv.text.toString(), binding.resetPasswordtv.text.toString())
        }
        binding.LoginBackBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}