package com.example.umc3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    private val albumList: MutableList<AlbumItem>,
    private val onMoreClick: (Int) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAlbum = itemView.findViewById<ImageView>(R.id.iv_album)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_album_title)
        val tvArtist = itemView.findViewById<TextView>(R.id.tv_album_artist)
        val btnMore = itemView.findViewById<ImageView>(R.id.btn_more)
        val switch = itemView.findViewById<Switch>(R.id.switch_onoff)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val item = albumList[position]

        holder.tvTitle.text = item.title
        holder.tvArtist.text = item.artist
        holder.ivAlbum.setImageResource(item.imageResId)

        // 더보기 버튼 클릭
        holder.btnMore.setOnClickListener {
            albumList.removeAt(position)
            notifyItemRemoved(position)
        }

        // Switch 상태 바인딩
        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = item.isSwitchOn
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            item.isSwitchOn = isChecked
        }
    }


    override fun getItemCount(): Int = albumList.size
}
