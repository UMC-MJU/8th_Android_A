package com.example.umc3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LibraryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlbumAdapter
    private val albumList = mutableListOf<AlbumItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_album)
        adapter = AlbumAdapter(albumList) { position ->
            albumList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadDummyData()
    }

    private fun loadDummyData() {
        albumList.add(AlbumItem("LILAC", "아이유 (IU)", R.drawable.lilac_album))
        albumList.add(AlbumItem("Bad Boy", "Red Velvet", R.drawable.badboy_album))
        albumList.add(AlbumItem("봄날", "방탄소년단", R.drawable.springday_album))
        albumList.add(AlbumItem("Weekend", "태연", R.drawable.weekend_album))
        albumList.add(AlbumItem("Celebrity", "아이유", R.drawable.celebrity_album))
        albumList.add(AlbumItem("예뻤어", "Day6(데이식스)", R.drawable.daysix_album))
        albumList.add(AlbumItem("Firefly", "엔플라잉(N.Flying)", R.drawable.firefly_album))

        adapter.notifyDataSetChanged()
    }

}
