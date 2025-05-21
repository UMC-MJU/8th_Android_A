package com.example.flo_clone.ui.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.SavedSongRVAdapter
import com.example.flo_clone.R
import com.example.flo_clone.Song
import com.example.flo_clone.databinding.FragmentLockerSavedsongBinding

class SavedSongFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedsongBinding
    private lateinit var songRVAdapter: SavedSongRVAdapter
    private val songList = arrayListOf(
        Song("Butter", "BTS (방탄소년단)", 0, 0, false, "", R.drawable.img_album_exp, false),
        Song("라일락", "아이유(IU)", 0, 0, false, "", R.drawable.img_album_exp2, false),
        Song("Next Level", "aespa", 0, 0, false, "", R.drawable.img_album_exp3, false),
        Song("Boy with Luv", "BTS (방탄소년단)", 0, 0, false, "", R.drawable.img_album_exp4, false),
        Song("BBoom BBoom(뿜뿜)", "MOMOLAND (모모랜드)", 0, 0, false, "", R.drawable.img_album_exp5, false)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        songRVAdapter = SavedSongRVAdapter()
        binding.lockerSavedSongRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

        // 어댑터에 데이터 전달
        songRVAdapter.addSongs(songList)

        // 클릭 리스너 연결
        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(song: Song) {
                // 리스트에서 삭제 후 어댑터 갱신
                songRVAdapter.removeSong(song)
            }
        })
    }
}
