package com.example.flo_clone.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.AuthService
import com.example.flo_clone.SignUpView
import com.example.flo_clone.User
import com.example.flo_clone.databinding.ActivitySignupBinding


class SignUpActivity : AppCompatActivity(), SignUpView {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding으로 레이아웃 연결
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [회원가입 구현하기] 버튼 클릭 시 회원가입 동작 연결
        binding.signUpSignUpBtn.setOnClickListener {
            signUp()
        }
    }

    // [DB 데이터 User에 Add] 유저 정보 입력값으로 User 객체 생성
    private fun getUser(): User {
        val email: String = binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val name: String = binding.signUpNameEt.text.toString()
        val pwd: String = binding.signUpPasswordEt.text.toString()

        return User(email, pwd, name)
    }

    // [회원가입 구현하기] 실제 회원가입 처리 함수
    private fun signUp() {
        // 이메일 입력 유효성 검사
        if (binding.signUpIdEt.text.toString().isEmpty() || binding.signUpDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호 확인 일치 검사
        if (binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // [회원가입 구현하기] AuthService를 통한 서버 통신
        val authService = AuthService()
        authService.setSignUpView(this)
        authService.signUp(getUser()) // User 데이터 전송

        Log.d("SIGNUP-ACT/ASYNC", "Hello, FLO")
    }

    // [회원가입 구현하기] 성공 콜백 → 액티비티 종료
    override fun onSignUpSuccess() {
        finish()
    }

    // [회원가입 구현하기] 실패
    override fun onSignUpFailure() {
    }
}