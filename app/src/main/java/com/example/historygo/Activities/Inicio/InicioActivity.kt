package com.example.historygo.Activities.Inicio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.Activities.Login
import com.example.historygo.Activities.RegisterActivity
import com.example.historygo.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar View Binding
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Corregir la sintaxis de los clics con lambdas
        binding.btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
