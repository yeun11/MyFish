package com.example.myfish.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myfish.MainActivity
import com.example.myfish.R
import com.example.myfish.auth.IntroActivity
import com.example.myfish.auth.UserModel
import com.example.myfish.databinding.ActivitySettingBinding
import com.example.myfish.store.WishlistActivity
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var binding : ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting)


        //로그아웃 버튼을 눌렀을 때
        binding.logoutBtn.setOnClickListener {

            auth.signOut()

            Toast.makeText(this, "로그아웃", Toast.LENGTH_LONG).show()

            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        //뒤로가기 버튼을 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //장바구니 버튼을 눌렀을 때
        binding.wishlistBtn.setOnClickListener{

            //장바구니로 이동
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)

        }

        //주문 배송 확인을 눌렀을 때
        binding.confirmOrderBtn.setOnClickListener{

            //주문 상세 페이지로 이동
            val intent = Intent(this, ConfirmOrderActivity::class.java)
            startActivity(intent)

        }

        //리뷰 작성을 눌렀을 때
        binding.writeReview.setOnClickListener{

            //리뷰작성 페이지로 이동
            val intent = Intent(this, WriteReviewActivity::class.java)
            startActivity(intent)

        }

        //닉네임 변경을 눌렀을 때
        binding.changeNickname.setOnClickListener {

            showNicknameDialog()

        }

        //회원 탈퇴를 눌렀을 때
        binding.withdraw.setOnClickListener {

            showWithdrawDialog()

        }

        getUserData(FBAuth.getUid())

    }

    //유저 정보 가져오기
    private fun getUserData(key: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(UserModel::class.java)
                    if(item!!.uid == key) {
                        binding.userUid.text = "UID : "+item!!.uid
                        binding.userName.text = item!!.nickname+"님 안녕하세요!"
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.userRef.addValueEventListener(postListener)
    }

    //회원 정보 제거
    private fun removeUserData(key: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(UserModel::class.java)
                    if(item!!.uid == key) {
                        //값 제거
                        FBRef.userRef
                            .child(dataModel.key.toString())
                            .removeValue()
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.userRef.addValueEventListener(postListener)
    }

    //닉네임 수정
    private fun changeNickname(nickname : String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(UserModel::class.java)
                    if(item!!.uid == FBAuth.getUid()) {
                        val userRef = FBRef.userRef.child(dataModel.key!!) // 수정하려는 데이터 위치
                        userRef.child("nickname").setValue(nickname) // nickname 수정
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        FBRef.userRef.addValueEventListener(postListener)
    }


    private fun showNicknameDialog(){ //닉네임 입력 다이얼로그
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.nickname_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val alertDialog = mBuilder.show()


        val nickname  = alertDialog.findViewById<EditText>(R.id.nicknameEdit)

        //완료 버튼을 눌렀을 때
        alertDialog.findViewById<Button>(R.id.finishBtn)?.setOnClickListener {
            //닉네임이 없는 경우
            if(nickname?.text.toString() == "" || nickname?.text.toString() == null){
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_LONG).show()
            }else{

                //데이터베이스에 값을 넣음
                changeNickname(nickname?.text.toString())

                getUserData(FBAuth.getUid())
                alertDialog.dismiss()

            }

        }

    }

    //회원 탈퇴 다이얼로그
    private fun showWithdrawDialog() {

        //AlertDialog 초기화
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)

        //다이얼로그 화면 설정
        val inflater : LayoutInflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.withdraw_dialog, null))

        //OK 이벤트
        builder.setPositiveButton("확인") {
            p0, p1 ->
            removeUserData(FBAuth.getUid()) //회원정보 제거
            auth.currentUser?.delete() //회원 제거

            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP //기존 액티비티를 날려버려서 기존화면이 뜨지 않게
            startActivity(intent)
        }

        //취소 이벤트
        builder.setNegativeButton("취소") {
            dialog, p1 -> dialog.cancel()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.show()
    }


}