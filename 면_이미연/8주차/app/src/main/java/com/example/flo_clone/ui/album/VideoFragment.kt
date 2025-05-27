package com.example.flo_clone.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flo_clone.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {
    private lateinit var binding: FragmentVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val videoViewModel = ViewModelProvider(this)[VideoViewModel::class.java]

        binding = FragmentVideoBinding.inflate(inflater, container, false)

        videoViewModel.text.observe(viewLifecycleOwner) {
            binding.videoTv.text = it
        }

        return binding.root
    }
}