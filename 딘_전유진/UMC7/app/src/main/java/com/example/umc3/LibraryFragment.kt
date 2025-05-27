package com.example.umc3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LibraryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlbumAdapter
    private lateinit var albumList: MutableList<AlbumItem>
    private lateinit var songDB: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        songDB = AppDatabase.getInstance(requireContext())!!
        albumList = mutableListOf()

        adapter = AlbumAdapter(albumList) { position ->
            albumList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }

        recyclerView = view.findViewById(R.id.rv_album)
        adapter = AlbumAdapter(albumList) { position ->
            albumList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }

        val btnSelectAll = view.findViewById<View>(R.id.btn_select_all)
        btnSelectAll.setOnClickListener {
            showBottomSheet()
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Firebase에서 좋아요 곡 불러오기
        val db = FirebaseDatabase.getInstance().getReference("likes")

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                albumList.clear()
                for (child in snapshot.children) {
                    val title = child.child("title").getValue(String::class.java) ?: continue
                    val artist = child.child("singer").getValue(String::class.java) ?: continue

                    // 이미지 리소스 매핑 (간단히 예시로 처리)
                    val imageRes = when (title) {
                        "ARMAGEDDON" -> R.drawable.armageddon_album
                        "LILAC" -> R.drawable.lilac_album
                        else -> R.drawable.lilac_album
                    }

                    albumList.add(AlbumItem(title, artist, imageRes))
                }
                adapter.setData(albumList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Firebase 로딩 실패", Toast.LENGTH_SHORT).show()
            }
        })

        loadDummyData()
    }

    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_library, null)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        val tvRemoveLikes = dialogView.findViewById<TextView>(R.id.tv_remove_likes)
        tvRemoveLikes.setOnClickListener {
            // Firebase에서 전체 좋아요 제거
            FirebaseDatabase.getInstance().getReference("likes").removeValue()

            // RoomDB에서도 isLike = false로 변경
            songDB.songDao().getLikedSongs().forEach {
                it.isLike = false
                songDB.songDao().updateSong(it)
            }

            // 리스트 갱신
            albumList.clear()
            adapter.setData(emptyList())
            dialog.dismiss()
        }

        dialog.show()
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
