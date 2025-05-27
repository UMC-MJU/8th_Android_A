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
            val albumTitle = tvTitle.text.toString()

            val db = AppDatabase.getInstance(requireContext())!!
            val album = db.albumDao().getAlbums().find { it.title == albumTitle }

            if (album != null) {
                val songs = db.songDao().getSongsByAlbum(album.id)

                if (songs.isNotEmpty()) {
                    val firstSong = songs[0]

                    // 첫 곡만 SharedPreferences에 저장
                    shared.edit()
                        .putString("title", firstSong.title)
                        .putString("singer", firstSong.singer)
                        .putInt("imageResId", album.coverImg)
                        .putString("lyrics", if (firstSong.title == "ARMAGEDDON") "We rise above it all (ARMAGEDDON)" else "내리는 꽃가루에 눈이 따끔해 아야")
                        .putBoolean("isPlaying", true)
                        .putInt("currentPosition", 0)
                        .putInt("songId", firstSong.id)
                        .apply()

                    // 플레이어 상태 갱신
                    MusicPlayerState.isPlaying = true
                    (activity as? MainActivity)?.updateMiniPlayerUI()

                    Toast.makeText(requireContext(), "앨범 전체 재생 시작!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "앨범에 곡이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "앨범 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
