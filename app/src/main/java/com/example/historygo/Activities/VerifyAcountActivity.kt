package com.example.historygo.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.historygo.R
import com.example.historygo.databinding.ActivityVerifyAcountBinding
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.Helper.BaseActivity

class VerifyAcountActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyAcountBinding
    private lateinit var cognito: Cognito
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyAcountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val email = intent.getStringExtra("email")

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
            } else {
                Log.e("Profile", "Error: Cognito no disponible")
            }
        }

        if(email != null){
            binding.editTextEmail.setText(email)
        }

        binding.verifyBtn.setOnClickListener{
            val email = binding.editTextEmail.text.toString()
            val verification = binding.editTextCode.text.toString()
            Log.d("testn", "El email: $email")
            if (email.isEmpty() || verification.isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.fields,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                cognito.confirmUser(binding.editTextEmail.text.toString(), binding.editTextCode.text.toString().replace(" ", ""))
            }
        }

        binding.textView2.setOnClickListener{
            val intent = android.content.Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}