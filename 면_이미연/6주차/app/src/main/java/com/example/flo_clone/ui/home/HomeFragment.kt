package com.example.flo_clone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo_clone.AlbumRVAdapter
import com.example.flo_clone.MainActivity
import com.example.flo_clone.R
import com.example.flo_clone.databinding.FragmentHomeBinding
import com.example.flo_clone.ui.album.Album
import com.example.flo_clone.ui.album.AlbumFragment
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var albumDates = ArrayList<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 더미 데이터 추가
        albumDates.apply {
            add(Album(title = "Butter", singer = "방탄소년단 (BTS)", coverImg = R.drawable.img_album_exp))
            add(Album(title = "Lilac", singer = "아이유 (IU)", coverImg = R.drawable.img_album_exp2))
            add(Album(title = "Next Level", singer = "에스파 (AESPA)", coverImg = R.drawable.img_album_exp3))
            add(Album(title = "Boy with Luv", singer = "방탄소년단 (BTS)", coverImg = R.drawable.img_album_exp4))
            add(Album(title = "BBoom BBoom", singer = "모모랜드 (MOMOLAND)", coverImg = R.drawable.img_album_exp5))
            add(Album(title = "Weekend", singer = "태연 (Tae Yeon)", coverImg = R.drawable.img_album_exp6))
        }

        // 어댑터 연결
        val albumRVAdapter = AlbumRVAdapter(albumDates)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 클릭 리스너 연결
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayAlbum(album: Album) {
                //  MiniPlayer에 앨범 정보 반영
                (activity as MainActivity).setMiniPlayer(album)
            }
        })

        // 배너 어댑터 연결
        val bannerAdapter = BannerViewPagerAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter

        return binding.root
    }

    // 앨범 클릭 시 상세 페이지로 이동
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
