package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo_clone.Song
import com.example.flo_clone.SongDatabase
import com.example.flo_clone.databinding.FragmentLockerSavedalbumBinding

class SavedAlbumFragment : Fragment() {

    lateinit var binding: FragmentLockerSavedalbumBinding
    lateinit var songDB: SongDatabase
    private lateinit var savedSongAdapter: SavedSongRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)
        songDB = SongDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val likedSongs: List<Song> = songDB.songDao().getLikedSongs(true)

        savedSongAdapter = SavedSongRVAdapter()
        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.lockerSavedSongRecyclerView.adapter = savedSongAdapter

        savedSongAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(song: Song) {
                song.isLike = false
                songDB.songDao().update(song)
                savedSongAdapter.removeSong(song)
            }
        })

        savedSongAdapter.addSongs(likedSongs as ArrayList<Song>)
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf!!.getInt("jwt", 0)
        Log.d("MAIN_ACT/GET_JWT", "jwt_token: $jwt")
        return jwt
    }

    fun clearAllLikedSongs() {
        val adapter = binding.lockerSavedSongRecyclerView.adapter as SavedSongRVAdapter
        adapter.clearSongs()
    }

}
