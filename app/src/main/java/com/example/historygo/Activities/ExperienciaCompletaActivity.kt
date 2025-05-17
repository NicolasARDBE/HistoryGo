package com.example.historygo.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.historygo.Activities.Fragments.MainFragment
import com.example.historygo.Activities.Fragments.ReproductorARFragment
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.Helper.LanguagePreference
import com.example.historygo.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ExperienciaCompletaActivity : BaseActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var  currentLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experiencia_completa)

        currentLanguage = LanguagePreference.getLanguage(this)

        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar)?.apply {
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).topMargin = systemBarsInsets.top
            }
            title = ""
        })

        // Inicializar ExoPlayer
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            if(currentLanguage == "es"){
                setMediaItem(MediaItem.fromUri("https://d3krfb04kdzji1.cloudfront.net/historia-chorro-v4.mp4"))
            } else {
                setMediaItem(MediaItem.fromUri("https://d3krfb04kdzji1.cloudfront.net/historia-chorro-v4-en.mp4"))
            }
            prepare()
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_OFF // Cambiado para permitir que termine

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED) {
                        runOnUiThread {
                            mostrarPopup() // Acción al finalizar el video
                        }
                    }
                }
            })
        }

        supportFragmentManager.commit {
            add(R.id.containerFragment, MainFragment(exoPlayer))
        }

        supportFragmentManager.commit {
            replace(R.id.containerFragment2, ReproductorARFragment(exoPlayer))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release() // Liberar recursos del ExoPlayer
    }

    fun Fragment.setFullScreen(
        fullScreen: Boolean = true,
        hideSystemBars: Boolean = true,
        fitsSystemWindows: Boolean = true
    ) {
        requireActivity().setFullScreen(
            this.requireView(),
            fullScreen,
            hideSystemBars,
            fitsSystemWindows
        )
    }

    fun mostrarPopup() {
        val dialogView = layoutInflater.inflate(R.layout.start_experience_popup, null)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        val btnDespues = dialogView.findViewById<Button>(R.id.btnDespues)
        val btnIniciar = dialogView.findViewById<Button>(R.id.btnIniciar)

        btnDespues.setOnClickListener {
            dialog.dismiss()
        }

        btnIniciar.setOnClickListener {
            val intent = Intent(this, Display360DegreeImage::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }
}

private fun Activity.setFullScreen(
    rootView: View,
    fullScreen: Boolean = true,
    hideSystemBars: Boolean = true,
    fitsSystemWindows: Boolean = true
) {
    rootView.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
        if (hasFocus) {
            WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindows)
            WindowInsetsControllerCompat(window, rootView).apply {
                if (hideSystemBars) {
                    if (fullScreen) {
                        hide(
                            WindowInsetsCompat.Type.statusBars() or
                                    WindowInsetsCompat.Type.navigationBars()
                        )
                    } else {
                        show(
                            WindowInsetsCompat.Type.statusBars() or
                                    WindowInsetsCompat.Type.navigationBars()
                        )
                    }
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }
}

private fun View.doOnApplyWindowInsets(action: (systemBarsInsets: Insets) -> Unit) {
    doOnAttach {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            action(insets.getInsets(WindowInsetsCompat.Type.systemBars()))
            WindowInsetsCompat.CONSUMED
        }
    }
}
