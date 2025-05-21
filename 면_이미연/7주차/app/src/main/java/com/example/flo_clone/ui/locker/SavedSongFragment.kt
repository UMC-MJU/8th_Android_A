package com.example.flo_clone.ui.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.SavedSongRVAdapter
import com.example.flo_clone.R
import com.example.flo_clone.Song
import com.example.flo_clone.SongDatabase
import com.example.flo_clone.databinding.FragmentLockerSavedsongBinding
import com.example.flo_clone.databinding.DialogDeleteLikeBinding

class SavedSongFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedsongBinding
    private lateinit var songRVAdapter: SavedSongRVAdapter
    private lateinit var songDB: SongDatabase
    private lateinit var songList: ArrayList<Song>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)
        songDB = SongDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
        initBottomSheetListener()
    }

    private fun initRecyclerView() {
        val likedSongs = songDB.songDao().getLikedSongs(true)
        songList = ArrayList(likedSongs)

        songRVAdapter = SavedSongRVAdapter()
        binding.lockerSavedSongRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

        songRVAdapter.addSongs(songList)

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(song: Song) {
                song.isLike = false
                songDB.songDao().update(song)
                songRVAdapter.removeSong(song)
            }
        })
    }

    private fun initBottomSheetListener() {
        binding.lockerSelectAllImgIv.setOnClickListener {
            showDeleteBottomSheet()
        }
    }

    private fun showDeleteBottomSheet() {
        val dialogBinding = DialogDeleteLikeBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.dialogDeleteBtn.setOnClickListener {
            for (song in songList) {
                song.isLike = false
                songDB.songDao().update(song)
            }
            songRVAdapter.clearSongs()
            bottomSheetDialog.dismiss()
        }

        dialogBinding.dialogCancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
}
