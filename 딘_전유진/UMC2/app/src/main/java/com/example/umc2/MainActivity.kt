package com.example.umc2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    // 초기 화면 설정
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // 앱 시작 시 홈 프래그먼트 먼저 보여줌
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment())
                .commit()
        }

        // 바텀 탭 클릭 시 프래그먼트 전환 + 애니메이션 적용
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.homeFragment -> HomeFragment()
                R.id.writeFragment -> WriteFragment()
                R.id.calendarFragment -> CalendarFragment()
                R.id.mypageFragment -> MyPageFragment()
                else -> return@setOnItemSelectedListener false
            }

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // 들어오는 화면
                    R.anim.slide_out_left   // 나가는 화면
                )
                .replace(R.id.nav_host_fragment, fragment)
                .commit()

            true
        }
    }
}
