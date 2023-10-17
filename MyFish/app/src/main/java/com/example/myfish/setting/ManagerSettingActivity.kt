package com.example.myfish.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.auth.IntroActivity
import com.example.myfish.auth.UserModel
import com.example.myfish.databinding.ActivityManagerSettingBinding
import com.example.myfish.store.OrderManageActivity
import com.example.myfish.store.ProductAddActivity
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ManagerSettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivityManagerSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_manager_setting)

        auth = Firebase.auth

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

        //주문관리를 눌렀을 때
        binding.orderManageBtn.setOnClickListener{

            val intent = Intent(this, OrderManageActivity::class.java)
            startActivity(intent)

        }

        //상품추가를 눌렀을 때
        binding.productAddBtn.setOnClickListener{

            val intent = Intent(this, ProductAddActivity::class.java)
            startActivity(intent)

        }

        getUserData(FBAuth.getUid())
    }


    //관리자 정보 가져오기
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
}