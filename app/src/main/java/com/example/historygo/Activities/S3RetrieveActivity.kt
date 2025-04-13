package com.example.historygo.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.amazonaws.mobileconnectors.apigateway.ApiRequest
import com.amazonaws.mobileconnectors.apigateway.ApiResponse
import com.bumptech.glide.Glide
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityS3RetrieveBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class S3RetrieveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityS3RetrieveBinding
    private val filename = "chorro-quevedo-antiguo.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityS3RetrieveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jwtToken = intent.getStringExtra("JWTTOKEN")

        binding.ReviewBtn.setOnClickListener {
            val intent = Intent(this, RatingManagement::class.java)
            startActivity(intent)
        }

        // Iniciar la solicitud en un hilo de fondo
        CoroutineScope(Dispatchers.IO).launch {
            fetchAndDisplayImage(jwtToken)
        }
    }

    private suspend fun fetchAndDisplayImage(jwtToken: String?) {
        try {
            // 1. Crear el cliente API
            val factory = ApiClientFactory()
            val apiClient = factory.build(HistorygoapiClient::class.java)

            // 2. Construir la solicitud a CloudFront
            val request = ApiRequest("historygo-api")
                .withHttpMethod(com.amazonaws.http.HttpMethodName.GET)
                .withPath("historygo-multimedia/$filename/cloudFront")
                .withHeaders(mapOf("Authorization" to jwtToken)) // JWT Token en headers

            // 3. Ejecutar la solicitud
            val response: ApiResponse = apiClient.execute(request)

            // 4. Descargar la imagen localmente
            val file = saveImageLocally(response.content)

            // 5. Mostrar la imagen en la UI
            withContext(Dispatchers.Main) {
                if (file != null) {
                    Glide.with(this@S3RetrieveActivity)
                        .load(file)
                        .into(binding.chorro)
                } else {
                    println("Error: No se pudo guardar la imagen")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                println("Error al recuperar la imagen: ${e.message}")
            }
        }
    }

    private fun saveImageLocally(inputStream: InputStream?): File? {
        return try {
            if (inputStream == null) return null

            val file = File(cacheDir, "imagen_cloudfront.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
