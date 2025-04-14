package com.example.flo_clone.ui.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo_clone.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    private lateinit var binding: FragmentLockerBinding
    private val tabs = listOf("저장한 곡", "음악파일")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        binding.lockerContentVp.adapter = LockerViewPagerAdapter(this)
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        return binding.root
    }
}