package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.databinding.ActivityChangePasswordBinding
import com.example.historygo.R

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var cognito: Cognito
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

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
                    this,
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