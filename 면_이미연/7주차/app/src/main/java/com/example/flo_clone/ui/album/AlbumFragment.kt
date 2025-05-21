package com.example.flo_clone.ui.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo_clone.*
import com.example.flo_clone.databinding.FragmentAlbumBinding
import com.example.flo_clone.ui.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.example.flo_clone.Album as DBAlbum

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    private lateinit var album: DBAlbum
    private var gson: Gson = Gson()

    private val tabs = listOf("수록곡", "상세정보", "영상")
    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // 앨범 JSON 파싱 및 null 체크
        val albumJson = arguments?.getString("album")
        val parsedAlbum = gson.fromJson(albumJson, DBAlbum::class.java)

        if (parsedAlbum == null) {
            Log.e("AlbumFragment", "album is null!")
            return binding.root
        }

        album = parsedAlbum
        isLiked = isLikedAlbum(album.id)

        setInit()
        setOnClickListeners()

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
                .commitAllowingStateLoss()
        }

        // 더미 앨범 리스트 (DBAlbum으로 명시)
        val albumList = arrayListOf(
            DBAlbum(id = 1, title = "BETTER", singer = "BoA", coverImg = R.drawable.img_album_exp),
            DBAlbum(id = 2, title = "마음이 말하는 행복", singer = "아이유", coverImg = R.drawable.img_album_exp2),
            DBAlbum(id = 3, title = "소녀시대", singer = "Girls' Generation", coverImg = R.drawable.img_album_exp3)
        )

        val albumAdapter = AlbumRVAdapter(albumList)
        binding.albumContentVp.adapter = albumAdapter

        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        return binding.root
    }

    private fun setInit() {
        binding.albumAlbumIv.setImageResource(album.coverImg ?: R.drawable.img_album_exp)
        binding.albumMusicTitleTv.text = album.title
        binding.albumSingerNameTv.text = album.singer

        binding.albumLikeIv.setImageResource(
            if (isLiked) R.drawable.ic_my_like_on else R.drawable.ic_my_like_off
        )
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf?.getInt("jwt", 0) ?: 0
    }

    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)
        songDB.albumDao().likeAlbum(like)
    }

    private fun disLikedAlbum(albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()
        songDB.albumDao().disLikedAlbum(userId, albumId)
    }

    private fun isLikedAlbum(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()
        val likeId: Int? = songDB.albumDao().isLikedAlbum(userId, albumId)
        return likeId != null
    }

    private fun setOnClickListeners() {
        val userId = getJwt()
        binding.albumLikeIv.setOnClickListener {
            if (isLiked) {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(album.id)
            } else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
            isLiked = !isLiked
        }
    }
}
