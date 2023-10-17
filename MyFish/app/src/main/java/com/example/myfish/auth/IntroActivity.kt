package com.example.myfish.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.myfish.MainActivity
import com.example.myfish.R
import com.example.myfish.databinding.ActivityIntroBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class IntroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivityIntroBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        //로그인 버튼을 눌렀을 때
        binding.loginBtn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //회원가입 버튼을 눌렀을 때
        binding.joinBtn.setOnClickListener{
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        //비회원으로 시작했을 때
        binding.noAccountBtn.setOnClickListener {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        showDialog() //닉네임 입력

                    } else {

                        Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()

                    }
                }
        }

    }

    private fun showDialog(){ //닉네임 입력 다이얼로그
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.nickname_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val alertDialog = mBuilder.show()
        alertDialog.setCancelable(false)   //다른 곳을 눌러 취소되는 것을 막음

        val nickname  = alertDialog.findViewById<EditText>(R.id.nicknameEdit)

        //완료 버튼을 눌렀을 때
        alertDialog.findViewById<Button>(R.id.finishBtn)?.setOnClickListener {
            //닉네임이 없는 경우
            if(nickname?.text.toString() == "" || nickname?.text.toString() == null){
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_LONG).show()
            }else{

                //데이터베이스에 값을 넣음
                FBRef.userRef
                    .push()
                    .setValue(UserModel(FBAuth.getUid(),nickname?.text.toString()))

                Toast.makeText(this, nickname?.text.toString()+"님 환영합니다.", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP //기존 액티비티를 날려버려서 기존화면이 뜨지 않게
                startActivity(intent)

            }


        }
    }
}