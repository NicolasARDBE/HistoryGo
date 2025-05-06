package com.example.historygo.Activities.Fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.historygo.databinding.FragmentReproductorBinding
import java.util.Timer
import java.util.TimerTask

class ReproductorFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null
    private var _binding: FragmentReproductorBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(audioUri: String, audioName: String): ReproductorFragment {
            val fragment = ReproductorFragment()
            val args = Bundle()
            args.putString("audioUri", audioUri)
            args.putString("audioName", audioName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReproductorBinding.inflate(inflater, container, false)

        val audioUriString = arguments?.getString("audioUri")
        val audioName = arguments?.getString("audioName")

        binding.textViewNombrePista.text = audioName ?: "Nombre desconocido"

        if (audioUriString != null) {
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer?.apply {
                    setDataSource(audioUriString)
                    setOnPreparedListener {
                        binding.progressBar.max = duration
                        start()
                        binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_pause)
                        startProgressUpdater()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("ReproductorFragment", "MediaPlayer error: what=$what, extra=$extra")
                        binding.textViewNombrePista.text = "Error al reproducir audio"
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("ReproductorFragment", "Excepción al reproducir: ${e.localizedMessage}")
                binding.textViewNombrePista.text = "Error al cargar audio"
            }
        } else {
            Log.e("ReproductorFragment", "Audio URI no proporcionado")
            binding.textViewNombrePista.text = "Audio no disponible"
        }

        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        binding.imageButtonClose.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.imageButtonPause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    it.start()
                    binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_pause)
                    startProgressUpdater()
                }
            }
        }

        binding.imageButtonRewind.setOnClickListener {
            mediaPlayer?.seekTo((mediaPlayer?.currentPosition ?: 0) - 5000)
        }

        binding.imageButtonForward.setOnClickListener {
            mediaPlayer?.seekTo((mediaPlayer?.currentPosition ?: 0) + 5000)
        }

        binding.imageButtonPrevious.setOnClickListener {
            mediaPlayer?.seekTo(0)
        }

        binding.imageButtonNext.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.duration ?: 0)
        }
    }

    private fun startProgressUpdater() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    mediaPlayer?.let {
                        binding.progressBar.progress = it.currentPosition
                    }
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
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
