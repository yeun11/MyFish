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
import com.example.myfish.board.BoardEditActivity
import com.example.myfish.board.BoardModel
import com.example.myfish.databinding.ActivityJoinBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        binding.backButton.setOnClickListener{

            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP //기존 액티비티를 날려버려서 기존화면이 뜨지 않게
            startActivity(intent)

        }

        //회원가입하기 버튼을 눌렀을 때
        binding.joinBtn.setOnClickListener{

            var isGoToJoin = true //가입조건을 만족하는지

            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea.text.toString()
            val password2 = binding.passwordChkArea.text.toString()

            //값이 비어있는지 확인
            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if(password1.isEmpty()) {
                Toast.makeText(this, "패스워드를 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }
            if(password2.isEmpty()) {
                Toast.makeText(this, "패스워드를 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            //패스워드와 패스워드확인 값이 같은지
            if(!password1.equals(password2)) {
                Toast.makeText(this, "비밀번호를 똑같이 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            //비밀번호가 6자리 미만일 때
            if(password1.length < 6) {
                Toast.makeText(this, "비밀번호를 6자리 이상 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(isGoToJoin) {

                auth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) { //회원가입 성공시

                            showDialog()

                        } else {

                            Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()

                        }
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
            if(nickname?.text.toString().equals("") || nickname?.text.toString() == null){
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