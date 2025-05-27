package com.example.flo_clone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.databinding.ActivityLoginBinding
import com.example.flo_clone.ui.SignUpActivity
import kotlin.jvm.java


class LoginActivity : AppCompatActivity(), LoginView {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 화면으로 이동
        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 버튼 클릭 시 로그인 시도
        binding.loginSignInBtn.setOnClickListener {
            login()
        }
    }


    private fun login() {
        if (binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val authService = AuthService()
        authService.setLoginView(this)

        // 로그인 요청
        authService.login(getUser())
    }

    // 로그인 요청에 필요한 User 객체 생성
    private fun getUser(): User {
        val email = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val password = binding.loginPasswordEt.text.toString()

        return User(email = email, password = password, name = "")
    }

    // 로그인 성공 시 메인 화면으로 이동
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // [jwt(userIdx) sharedPreference에 저장] - 정수형 jwt 저장
    private fun saveJwt(jwt: Int) {
        val spf = getSharedPreferences("auth" , MODE_PRIVATE)
        val editor = spf.edit()

        editor.putInt("jwt", jwt)
        editor.apply()
    }

    // [jwt(userIdx) sharedPreference에 저장] - 문자열 jwt 저장
    private fun saveJwt2(jwt: String) {
        val spf = getSharedPreferences("auth2" , MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("jwt", jwt)
        editor.apply()
    }

    // [LoginActivity 로그인 구현하기]
    override fun onLoginSuccess(code : Int , result: Result) {
        when (code) {
            1000 -> {
                saveJwt2(result.jwt)
                startMainActivity()

            }
        }
    }
    // [LoginActivity 로그인 구현하기] - 로그인 실패 콜백
    override fun onLoginFailure() {

    }
}