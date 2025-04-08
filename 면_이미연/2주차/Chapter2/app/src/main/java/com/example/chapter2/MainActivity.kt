package com.example.chapter2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter2.databinding.ActivityMainBinding
import com.example.chapter2.fragments.HomeFragment
import com.example.chapter2.fragments.LocationFragment
import com.example.chapter2.fragments.ChatFragment
import com.example.chapter2.fragments.PersonFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 화면: 홈
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()

        // BottomNavigation 클릭 시 fragment 전환 + 슬라이드 애니메이션
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.fragment_home -> HomeFragment()
                R.id.fragment_location -> LocationFragment()
                R.id.fragment_chat -> ChatFragment()
                R.id.fragment_person -> PersonFragment()
                else -> null
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    .replace(R.id.main_container, it)
                    .commit()
            }

            true
        }
    }
}
