package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo_clone.Song
import com.example.flo_clone.databinding.ItemSongBinding

class SavedSongRVAdapter : RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {

    private var songs = ArrayList<Song>()

    // 클릭 이벤트를 위한 interface 선언
    interface MyItemClickListener {
        fun onRemoveSong(song: Song)
    }

    private lateinit var myItemClickListener: MyItemClickListener

    // 외부에서 리스너 연결할 수 있도록 함수 정의
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])

        // 보관함의 [...] 버튼 클릭 시 삭제 콜백
        holder.binding.itemSongMoreIv.setOnClickListener {
            myItemClickListener.onRemoveSong(songs[position])
        }
    }

    override fun getItemCount(): Int = songs.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSong(song: Song) {
        songs.remove(song)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSongs() {
        songs.clear()
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.itemSongImgIv.setImageResource(song.coverImg!!)
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv.text = song.singer

            // 이 노래의 이전 스위치 상태를 보여줌
            binding.itemSongSwitch.isChecked = song.isSwitchOn

            // 바꾼 스위치 상태를 데이터에 반영
            binding.itemSongSwitch.setOnCheckedChangeListener { _, isChecked ->
                song.isSwitchOn = isChecked
            }
        }
    }
}
