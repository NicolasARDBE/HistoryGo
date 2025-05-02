package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.databinding.ActivityChangePasswordBinding
import com.example.historygo.R

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var cognito: Cognito
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
            } else {
                Log.e("Profile", "Error: Cognito no disponible")
            }
        }

        binding.resetPasswordBtn.setOnClickListener{
            val oldPassword = binding.resetPasswordtvOld.text.toString()
            val newPassword = binding.resetPasswordtvNew.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    R.string.fields,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                cognito.changePassword(binding.resetPasswordtvOld.text.toString(), binding.resetPasswordtvNew.text.toString())
            }
        }

        binding.LoginBackBtn.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }
}