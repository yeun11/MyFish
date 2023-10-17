package com.example.myfish.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.auth.IntroActivity
import com.example.myfish.auth.UserModel
import com.example.myfish.databinding.ActivityBoardEditBinding
import com.example.myfish.fragments.TalkFragment
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class BoardEditActivity : AppCompatActivity() {

    private lateinit var key: String

    private lateinit var binding: ActivityBoardEditBinding

    private lateinit var writerUid : String
    private var nickname : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)

        //게시판 데이터 받아오기
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)
        getUserNickname()

        //수정완료
        binding.editBtn.setOnClickListener {
            editBoardData(key)
        }

        //뒤로가기
        binding.backButton.setOnClickListener{

            finish()

        }

    }

    private fun editBoardData(key : String){

        FBRef.boardRef //게시판 데이터 수정
            .child(key)
            .setValue(
                BoardModel(binding.titleArea.text.toString(),
                    binding.contentArea.text.toString(),
                    writerUid,
                    nickname,
                    FBAuth.getTime())
            )

        Toast.makeText(this, "수정완료", Toast.LENGTH_LONG).show()

        finish()

    }


    //기존 이미지 가져오기
    private fun getImageData(key: String) {

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.imageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {

                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)

            } else {

            }
        })


    }

    //기존 데이터 가져오기
    private fun getBoardData(key: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)

                    binding.titleArea.setText(dataModel?.title)
                    binding.contentArea.setText(dataModel?.content)
                    writerUid = dataModel!!.uid

                } catch (e: Exception) {

                }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }

    //닉네임 가져오기
    private fun getUserNickname() {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(UserModel::class.java)
                    if(item!!.uid == FBAuth.getUid()) {
                        nickname = item.nickname
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }

        }
        FBRef.userRef.addValueEventListener(postListener)

    }


}