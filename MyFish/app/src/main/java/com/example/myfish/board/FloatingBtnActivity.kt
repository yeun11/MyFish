package com.example.myfish.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.databinding.ActivityFloatingBtnBinding

class FloatingBtnActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFloatingBtnBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_floating_btn)

        //x 버튼을 눌렀을 때
        binding.clearBtn.setOnClickListener{
            finish()
        }

        //내가 쓴 글 버튼을 눌렀을 때
        binding.myPostBtn.setOnClickListener {
            val intent = Intent(this, MyPostActivity::class.java)
            startActivity(intent)
            finish()
        }

        //글쓰기 버튼을 눌렀을 때
        binding.writeBtn.setOnClickListener {
            val intent = Intent(this, BoardWriteActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}