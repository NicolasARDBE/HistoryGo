package com.example.historygo.Activities.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.historygo.Activities.ExpererienceMenuActivity
import com.example.historygo.Activities.Profile
import com.example.historygo.databinding.FragmentNavegationBarBinding


class NavegationBarFragment : Fragment() {

    private var _binding: FragmentNavegationBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavegationBarBinding.inflate(inflater, container, false)

        // Redireccionar cuando se presionan los botones
        binding.btnHome.setOnClickListener {
            startActivity(Intent(requireContext(), ExpererienceMenuActivity::class.java))
        }

        /*
        binding.btnFavorites.setOnClickListener {
            startActivity(Intent(requireContext(), FavoritesActivity::class.java))
        }

        binding.btnArchive.setOnClickListener {
            startActivity(Intent(requireContext(), ArchiveActivity::class.java))
        }
        */
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(requireContext(), Profile::class.java))
        }


        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}