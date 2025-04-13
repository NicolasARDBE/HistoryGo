package com.example.historygo.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.awsServices.Cognito
import com.example.historygo.awsServices.CognitoManager
import com.example.historygo.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var cognito: Cognito
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        cognito = CognitoManager.getInstance(applicationContext).getCognito()!!
        setContentView(binding.root)

        binding.StartProcessBtn.setOnClickListener(){
            cognito.forgotPassword(binding.Correotv.text.toString())
        }

        binding.resetPasswordBtn.setOnClickListener(){
            cognito.confirmForgotPassword(binding.Codetv.text.toString(), binding.resetPasswordtv.text.toString())
        }
        binding.LoginBackBtn.setOnClickListener(){
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}