package com.example.historygo.Activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Activities.Fragments.ReproductorFragment
import com.example.historygo.Adapters.ImageAdapter
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityExperienciaCompletaBinding

class ExperienciaCompletaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExperienciaCompletaBinding
    val cloudFrontBaseUrl = "https://d3krfb04kdzji1.cloudfront.net/"

    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis = 5000L
    private var currentPage = 0
    private var isUserInteracting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienciaCompletaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)
        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        // Márgenes para la pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Solo aplica padding a los lados y parte superior
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Márgenes para la pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Solo aplica padding a los lados y parte superior
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        viewPager = findViewById(R.id.viewPager)
        val urls = listOf(
            cloudFrontBaseUrl + "images/chorro-quevedo-antiguo.jpg",
            cloudFrontBaseUrl + "images/chorro-quevedo-antiguo-2.png",
            cloudFrontBaseUrl + "images/chorro-quevedo-antiguo-3.jfif",
            cloudFrontBaseUrl + "images/chorro-quevedo-antiguo-40.jfif",
            cloudFrontBaseUrl + "images/chorro-quevedo-antiguo-85.jpeg",
            cloudFrontBaseUrl + "images/chorro-quevedo-antiguo-96.jpg"
        )

        viewPager.adapter = ImageAdapter(urls)

        startAutoScroll()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                isUserInteracting = state != ViewPager2.SCROLL_STATE_IDLE
                if (!isUserInteracting) {
                    currentPage = viewPager.currentItem
                }
            }
        })

        if (jwtToken != null) {
            setupAudioPlayback(client, jwtToken)
        }
    }

    private fun startAutoScroll() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isUserInteracting && viewPager.adapter != null) {
                    val itemCount = viewPager.adapter!!.itemCount
                    currentPage = (currentPage + 1) % itemCount
                    viewPager.setCurrentItem(currentPage, true)
                }
                handler.postDelayed(this, delayMillis)
            }
        }, delayMillis)
    }

    private fun setupAudioPlayback(client: HistorygoapiClient, jwtToken: String) {
        val audioName = "Chorro de Quevedo"
        val audioKey = "guion-v2-chorro.mp3"
        val audioUrl = "$cloudFrontBaseUrl$audioKey"

        val fragment = ReproductorFragment.newInstance(audioUrl, audioName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, fragment)
            .commit()
    }
}