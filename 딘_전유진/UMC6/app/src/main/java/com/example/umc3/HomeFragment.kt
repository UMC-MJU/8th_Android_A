package com.example.umc3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ivAlbum = view.findViewById<ImageView>(R.id.iv_album)
        val title = view.findViewById<TextView>(R.id.tv_album_title)
        val singer = view.findViewById<TextView>(R.id.tv_singer)
        val btnPlay = view.findViewById<ImageView>(R.id.btn_album_play)
        val tvTitle = view.findViewById<TextView>(R.id.tv_album_title)
        val tvSinger = view.findViewById<TextView>(R.id.tv_singer)

        btnPlay.setOnClickListener {
            val shared = requireActivity().getSharedPreferences("music", AppCompatActivity.MODE_PRIVATE)
            shared.edit()
                .putString("title", tvTitle.text.toString())
                .putString("singer", tvSinger.text.toString())
                .putInt("imageResId", R.drawable.armageddon_album)
                .putString("lyrics", "Armageddon\n" +
                        "       Shoot")
                .putBoolean("isPlaying", true)
                .putInt("currentPosition", 0)
                .apply()
            // MiniPlayer UI 강제 갱신
            (activity as? MainActivity)?.updateMiniPlayerUI()
            Toast.makeText(requireContext(), "재생 시작!", Toast.LENGTH_SHORT).show()

            MusicPlayerState.isPlaying = true
            (activity as? MainActivity)?.updateMiniPlayerUI()
        }

        ivAlbum.setOnClickListener {
            val albumFragment = AlbumFragment.newInstance(
                title.text.toString(),
                singer.text.toString()
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, albumFragment)
                .addToBackStack(null)
                .commit()
        }

    }
}
