package com.example.flo_clone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo_clone.Album
import com.example.flo_clone.AlbumRVAdapter
import com.example.flo_clone.MainActivity
import com.example.flo_clone.R
import com.example.flo_clone.SongDatabase
import com.example.flo_clone.databinding.FragmentHomeBinding
import com.example.flo_clone.ui.album.AlbumFragment
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var albumDates = ArrayList<Album>() // DB용 Album 사용

    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!
        albumDates.addAll(songDB.albumDao().getAlbums()) // DB에서 앨범 불러오기

        val albumRVAdapter = AlbumRVAdapter(albumDates)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 클릭 리스너 구현
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayAlbum(album: Album) {

                // 앨범 ID 기반으로 해당 수록곡 전체 재생
                val songs = songDB.songDao().getSongsByAlbumId(album.id)
                if (songs.isNotEmpty()) {
                    (activity as MainActivity).setMiniPlayer(songs)
                }
            }
        })

        // 배너 ViewPager 연결
        val bannerAdapter = BannerViewPagerAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter

        return binding.root
    }

    // 앨범 클릭 시 상세 프래그먼트로 전환
    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }
}
