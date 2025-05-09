package com.example.historygo.Activities.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import com.example.historygo.databinding.FragmentReproductorBinding
import java.util.Timer
import java.util.TimerTask

class ReproductorARFragment(private val exoPlayer: ExoPlayer) : Fragment() {
    private var timer: Timer? = null
    private var _binding: FragmentReproductorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReproductorBinding.inflate(inflater, container, false)

        // Configurar el nombre de la pista si está disponible
        val audioName = arguments?.getString("audioName")
        binding.textViewNombrePista.text = audioName ?: "Historia del Chorro de Quevedo"

        // Configurar los controles multimedia
        binding.imageButtonPause.setOnClickListener {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_play)
            } else {
                exoPlayer.play()
                binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_pause)
                startProgressUpdater()
            }
        }

        binding.imageButtonRewind.setOnClickListener {
            exoPlayer.seekTo((exoPlayer.currentPosition - 5000).coerceAtLeast(0))
        }

        binding.imageButtonForward.setOnClickListener {
            exoPlayer.seekTo((exoPlayer.currentPosition + 5000).coerceAtMost(exoPlayer.duration))
        }

        binding.imageButtonPrevious.setOnClickListener {
            exoPlayer.seekTo(0)
        }

        binding.imageButtonNext.setOnClickListener {
            exoPlayer.seekTo(exoPlayer.duration)
        }

        binding.imageButtonClose.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.progressBar.max = exoPlayer.duration.toInt()
        startProgressUpdater()

        return binding.root
    }

    private fun startProgressUpdater() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    binding.progressBar.progress = exoPlayer.currentPosition.toInt()
                }
            }
        }, 0, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}