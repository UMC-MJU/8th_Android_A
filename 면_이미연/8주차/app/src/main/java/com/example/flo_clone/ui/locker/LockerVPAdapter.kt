package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo_clone.ui.locker.MusicFileFragment
import com.example.flo_clone.ui.locker.SavedSongFragment

class LockerVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    val savedAlbumFragment = SavedAlbumFragment()

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SavedSongFragment()
            1 -> MusicFileFragment()
            2 -> savedAlbumFragment
            else -> Fragment()
        }
    }
}




