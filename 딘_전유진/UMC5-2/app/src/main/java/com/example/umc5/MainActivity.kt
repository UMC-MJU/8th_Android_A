package com.example.umc5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var editTextMemo: EditText
    private var savedMemo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextMemo = findViewById(R.id.editTextMemo)
        val buttonNext: Button = findViewById(R.id.buttonNext)

        buttonNext.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("memo", editTextMemo.text.toString())
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        savedMemo = editTextMemo.text.toString()
    }

    override fun onResume() {
        super.onResume()
        if (savedMemo.isNotEmpty()) {
            editTextMemo.setText(savedMemo)
        }
    }

    override fun onRestart() {
        super.onRestart()
        AlertDialog.Builder(this)
            .setTitle("다시 작성할까요?")
            .setMessage("이전에 작성한 메모가 있습니다. 새로 작성하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                // 아무 처리 없이 작성 유지
            }
            .setNegativeButton("아니오") { _, _ ->
                savedMemo = ""
                editTextMemo.setText("")
            }
            .show()
    }
}