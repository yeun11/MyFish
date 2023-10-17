package com.example.myfish.board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R
import com.example.myfish.auth.UserModel
import com.example.myfish.databinding.ActivityBoardWriteBinding
import com.example.myfish.fragments.TalkFragment
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBoardWriteBinding

    private var isImageUpload = false
    private var nickname : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)

        getUserNickname()

        //글 쓰기 버튼을 눌렀을 때
        binding.writeBtn.setOnClickListener{

            val title = binding.titleArea.text.toString() //제목 부분 글을 받아옴
            val content = binding.contentArea.text.toString() //내용 부분 글을 받아옴
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            val key = FBRef.boardRef.push().key.toString()

            FBRef.boardRef //데이터베이스에 값을 넣음
                .child(key)
                .setValue(BoardModel(title,content,uid,nickname,time,0))

            if(isImageUpload == true) {
                imageUpload(key) //이미지 이름을 문서의 key값으로 해서 이미지 정보를 찾기 쉽게 함
            }

            finish()

        }

        //뒤로가기 버튼을 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //이미지 업로드 버튼
        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }

    }

    //이미지 업로드 하기
    private fun imageUpload(key : String) {

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key+".png") //이미지 이름을 문서의 key값으로 설정

        // Get the data from an ImageView as bytes
        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }

    }

    //이미지 가져오기
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 100) {
            binding.imageArea.setImageURI(data?.data)
        }
    }

    //유저 닉네임 가져오기
    private fun getUserNickname(){

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