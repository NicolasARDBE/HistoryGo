package com.example.historygo.Activities.Fragments

import android.media.MediaPlayer
import android.net.Uri
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
            args.putString("audioName", audioName) // Store the audio name
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
        val audioName = arguments?.getString("audioName") // Retrieve the audio name

        if (audioUriString != null) {
            val audioUri = Uri.parse(audioUriString)
            mediaPlayer = MediaPlayer.create(context, audioUri)
            binding.progressBar.max = mediaPlayer?.duration ?: 0

            // Set the audio name to textViewNombrePista
            binding.textViewNombrePista.text = audioName ?: "Nombre Desconocido"  // Use a default if null

        } else {
            Log.e("ReproductorFragment", "Audio URI not provided")
            binding.imageButtonPause.isEnabled = false
        }

        binding.imageButtonClose.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.imageButtonPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer?.start()
                binding.imageButtonPause.setImageResource(android.R.drawable.ic_media_pause)
                startProgressUpdater()
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

        return binding.root
    }

    private fun startProgressUpdater() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    binding.progressBar.progress = mediaPlayer?.currentPosition ?: 0
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
        mediaPlayer?.release()
        mediaPlayer = null
        timer?.cancel()
    }
}