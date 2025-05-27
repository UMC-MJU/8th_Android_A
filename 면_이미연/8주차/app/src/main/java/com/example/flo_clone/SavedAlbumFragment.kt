package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo_clone.Album
import com.example.flo_clone.SongDatabase
import com.example.flo_clone.databinding.FragmentLockerSavedalbumBinding

class SavedAlbumFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedalbumBinding
    lateinit var albumDB: SongDatabase
    private lateinit var albumRVAdapter: AlbumLockerRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)
        albumDB = SongDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    fun clearAllLikedSongs() {
        albumRVAdapter.clearAlbums()
    }

    private fun initRecyclerview() {
        binding.lockerSavedSongRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        albumRVAdapter = AlbumLockerRVAdapter()

        albumRVAdapter.setMyItemClickListener(object : AlbumLockerRVAdapter.MyItemClickListener {
            override fun onRemoveAlbum(albumId: Int) {
                albumDB.albumDao().disLikedAlbum(getJwt(), albumId)
                refreshLikedAlbums()
            }
        })

        binding.lockerSavedSongRecyclerView.adapter = albumRVAdapter
        refreshLikedAlbums()
    }

    private fun refreshLikedAlbums() {
        val likedAlbums: List<Album> = albumDB.albumDao().getLikedAlbums(getJwt())
        albumRVAdapter.addAlbums(ArrayList(likedAlbums)) // 안전한 ArrayList 변환
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf?.getInt("jwt", 0) ?: 0
        Log.d("MAIN_ACT/GET_JWT", "jwt_token: $jwt")
        return jwt
    }

    override fun onResume() {
        super.onResume()
        refreshLikedAlbums() // 저장 앨범 목록 새로고침
    }

}
