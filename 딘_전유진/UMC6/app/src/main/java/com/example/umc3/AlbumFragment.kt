package com.example.umc3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class AlbumFragment : Fragment() {

    companion object {
        fun newInstance(title: String, singer: String): AlbumFragment {
            val fragment = AlbumFragment()
            val args = Bundle().apply {
                putString("title", title)
                putString("singer", singer)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var isMixChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = arguments?.getString("title") ?: "제목 없음"
        val singer = arguments?.getString("singer") ?: "가수 없음"

        val tvTitle = view.findViewById<TextView>(R.id.tv_album_title)
        val tvSinger = view.findViewById<TextView>(R.id.tv_album_singer)
        val albumImage = view.findViewById<ImageView>(R.id.album_img)
        val btnMix = view.findViewById<ImageView>(R.id.btn_my_mix)
        val btnBack = view.findViewById<ImageView>(R.id.btn_album_back)

        // 초기 텍스트 설정
        tvTitle.text = title
        tvSinger.text = singer

        // MIX 버튼 클릭 시 이미지 및 텍스트 변경
        btnMix.setOnClickListener {
            isMixChanged = !isMixChanged
            if (isMixChanged) {
                albumImage.setImageResource(R.drawable.mix_album_img)
                tvTitle.text = "Next Level"
                tvSinger.text = "aespa (에스파)"
            } else {
                albumImage.setImageResource(R.drawable.lilac_album)
                tvTitle.text = title
                tvSinger.text = singer
            }
        }

        // 뒤로가기
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
