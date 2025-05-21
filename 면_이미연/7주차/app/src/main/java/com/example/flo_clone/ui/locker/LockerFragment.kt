package com.example.flo

import com.example.flo_clone.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo_clone.LoginActivity
import com.example.flo_clone.MainActivity
import com.example.flo_clone.SongDatabase
import com.example.flo_clone.databinding.FragmentLockerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding
    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        // ViewPager 연결
        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        // 로그인/로그아웃 버튼 클릭
        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        // 전체선택 클릭 시 BottomSheetDialog 표시
        binding.lockerSelectAllTv.setOnClickListener {
            showDislikeAllDialog()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    private fun initViews() {
        val jwt = getJwt()

        if (jwt == 0) {
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        } else {
            binding.lockerLoginTv.text = "로그아웃"
            binding.lockerLoginTv.setOnClickListener {
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf?.getInt("jwt", 0) ?: 0
    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        spf?.edit()?.remove("jwt")?.apply()
    }

    // BottomSheetDialog 내부 처리 함수
    private fun showDislikeAllDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_dislike_all, null)
        dialog.setContentView(view)

        val confirmBtn = view.findViewById<Button>(R.id.dialogCancelBtn)
        confirmBtn.setOnClickListener {
            val db = SongDatabase.getInstance(requireContext())!!
            val likedSongs = db.songDao().getLikedSongs(true)

            // 1. DB isLike = false로 전체 업데이트
            likedSongs.forEach {
                it.isLike = false
                db.songDao().update(it)
            }

            // 2. ViewPager 내 프래그먼트 접근해 어댑터 비움
            val savedFragment = childFragmentManager.findFragmentByTag("f0")
            if (savedFragment is SavedAlbumFragment) {
                savedFragment.clearAllLikedSongs()
            }

            // 3. 다이얼로그 닫기
            dialog.dismiss()
        }

        dialog.show()
    }
}
