package com.example.flo_clone.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flo_clone.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        binding = FragmentDetailBinding.inflate(inflater, container, false)

        detailViewModel.text.observe(viewLifecycleOwner) {
            binding.detailTv.text = it
        }

        return binding.root
    }
}