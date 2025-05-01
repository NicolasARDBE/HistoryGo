package com.example.historygo.Activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.R
import com.example.historygo.databinding.ActivityVerifyAcountBinding
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager

class VerifyAcountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyAcountBinding
    private lateinit var cognito: Cognito
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyAcountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verify_acount)

        val userId = intent.getStringExtra("userId")

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
            } else {
                Log.e("Profile", "Error: Cognito no disponible")
            }
        }

        binding.button.setOnClickListener{
            cognito.confirmUser(userId, binding.editTextText.getText().toString().replace(" ", ""));
        }
    }
}