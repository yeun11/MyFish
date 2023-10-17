package com.example.myfish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.myfish.setting.ManagerSettingActivity
import com.example.myfish.setting.SettingActivity
import com.example.myfish.utils.FBAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.settingBtn).setOnClickListener {

            if(FBAuth.checkManager()){ //관리자인 경우
                val intent = Intent(this, ManagerSettingActivity::class.java)
                startActivity(intent)
            }else {//회원인 경우
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }

        }

        findViewById<ImageView>(R.id.settingBtn).bringToFront();
    }

}