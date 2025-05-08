package com.example.historygo.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var cognito: Cognito

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)

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