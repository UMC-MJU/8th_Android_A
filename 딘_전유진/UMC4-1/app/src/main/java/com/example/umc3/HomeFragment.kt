package com.example.umc3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ivAlbum = view.findViewById<ImageView>(R.id.iv_album)
        val title = view.findViewById<TextView>(R.id.tv_album_title)
        val singer = view.findViewById<TextView>(R.id.tv_singer)

        ivAlbum.setOnClickListener {
            val albumFragment = AlbumFragment.newInstance(
                title.text.toString(),
                singer.text.toString()
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, albumFragment)
                .addToBackStack(null)
                .commit()
        }

    }
}
