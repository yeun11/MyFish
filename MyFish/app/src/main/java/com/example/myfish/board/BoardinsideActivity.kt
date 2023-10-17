package com.example.myfish.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityBoardinsideBinding
import com.bumptech.glide.Glide
import com.example.myfish.auth.UserModel
import com.example.myfish.comment.CommentModel
import com.example.myfish.comment.CommentRVAdapter
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardinsideActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityBoardinsideBinding

    private lateinit var key : String
    private var nickname : String = ""

    private val commentDataList = mutableListOf<CommentModel>()

    private lateinit var  commentAdapter : CommentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_boardinside)

        //수정,삭제하기
        binding.boardSettingIcon.setOnClickListener{
            showDialog()
        }

        // TalkFragment에서 보낸 게시판 데이터 받기
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)
        getUserNickname()
        getCommentData(key)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //댓글 입력
        binding.commentBtn.setOnClickListener {
            if(binding.commentArea.text.toString().isEmpty()){
                Toast.makeText(this, "댓글을 입력해 주세요", Toast.LENGTH_LONG).show()
            } else {
                insertComment(key)
            }
        }

        //댓글 어댑터 연결
        commentAdapter = CommentRVAdapter(commentDataList)
        binding.commentRv.adapter = commentAdapter
        binding.commentRv.layoutManager = LinearLayoutManager(this)



    }

    fun getCommentData(key : String){ //댓글 불러오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    commentDataList.clear()

                    var comcount = 0 //댓글 개수
                    for (dataModel in dataSnapshot.children) {
                        comcount += 1
                        binding.noComment.isVisible = false

                        val item =
                            dataModel.getValue(CommentModel::class.java) //commentmodel의 형태로 받아오기
                        commentDataList.add(item!!)
                    }

                    binding.commentTxt.text = "댓글 • "+comcount
                    commentAdapter.notifyDataSetChanged() //데이터 동기화

                }catch(e : Exception) {}
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.commentRef.child(key).addValueEventListener(postListener)



    }

    fun insertComment(key : String){ //댓글을 파이어베이스에 저장

        FBRef.commentRef   //comment
            .child(key)    //BoardKey
            .push()        //키값 생성
            .setValue(
                CommentModel(
                    nickname, //닉네임
                    binding.commentArea.text.toString(), //comment값을 집어 넣음
                    FBAuth.getTime()  //시간 넣기
                )
            )

        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("")

        //댓글을 입력할 때마다 댓글의 개수 변경
        var comcount = 0
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    comcount += 1
                }

                FBRef.boardRef.child(key).child("commentCount").setValue(comcount)
                binding.commentTxt.text = "댓글 • "+comcount

            }
            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.commentRef.child(key).addValueEventListener(postListener)

    }

    private fun showDialog(){ //수정/삭제 다이얼로그
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val alertDialog = mBuilder.show()

        //수정하기 버튼을 눌렀을 때
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key",key)
            startActivity(intent)
        }
        //삭제하기 버튼을 눌렀을 때
        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    private fun getImageData(key : String){ //게시판 이미지 불러오기

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.getImageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때

                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)

            } else { //이미지가 없을 때
                binding.getImageArea.isVisible = false
            }
        })


    }


    private fun getBoardData(key : String){ //게시판 데이터 불러오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try{

                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)

                    binding.titleArea.text = dataModel!!.title
                    binding.textArea.text = dataModel!!.content
                    binding.timeArea.text = dataModel!!.time
                    binding.nickname.text = dataModel!!.name

                    val myUid = FBAuth.getUid()
                    val writerUid = dataModel.uid

                    if(myUid.equals(writerUid)){ //본인이 쓴 글일 때 (수정,삭제) 나타내기
                        binding.boardSettingIcon.isVisible = true
                    }

                } catch (e : Exception){

                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)



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