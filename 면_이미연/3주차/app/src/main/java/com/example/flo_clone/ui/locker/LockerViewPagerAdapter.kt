package com.example.flo_clone.ui.locker

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LockerViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SavedFragment()
            1 -> MusicFileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}