package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo_clone.Album
import com.example.flo_clone.databinding.ItemLockerAlbumBinding

class AlbumLockerRVAdapter : RecyclerView.Adapter<AlbumLockerRVAdapter.ViewHolder>() {

    private val albums = ArrayList<Album>()

    // 앨범 클릭 인터페이스 정의
    interface MyItemClickListener {
        fun onRemoveAlbum(albumId: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLockerAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])

        // [···] 버튼 클릭 시 좋아요 해제 요청 및 리스트에서 제거
        holder.binding.itemAlbumMoreIv.setOnClickListener {
            mItemClickListener.onRemoveAlbum(albums[position].id)
            removeAlbum(position)
        }
    }

    override fun getItemCount(): Int = albums.size

    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAlbum(position: Int) {
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAlbums() {
        albums.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLockerAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemAlbumImgIv.setImageResource(album.coverImg!!)
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
        }
    }

}