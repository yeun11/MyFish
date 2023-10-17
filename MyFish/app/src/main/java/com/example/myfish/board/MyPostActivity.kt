package com.example.myfish.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityMyPostBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMyPostBinding

    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()

    private lateinit var boardRVAdapter: BoardRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_post)

        getFBBoardData()

        //게시판 리사이클러뷰 어댑터 연결
        boardRVAdapter = BoardRVAdapter(boardDataList)
        boardRVAdapter.itemClick = object : BoardRVAdapter.ItemClick { //게시물 하나를 클릭했을 때 그 게시물로 이동
            override fun onClick(view: View, position: Int) {

                val intent = Intent(this@MyPostActivity, BoardinsideActivity::class.java)
                intent.putExtra("key", boardKeyList[position])
                startActivity(intent)
            }
        }
        binding.boardRV.adapter = boardRVAdapter
        binding.boardRV.layoutManager = LinearLayoutManager(this)

        //뒤로가기 버튼을 눌렀을 때
        binding.backButton.setOnClickListener{
            finish()
        }



    }

    private fun getFBBoardData(){ //파이어베이스에서 게시판 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                boardDataList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(BoardModel::class.java)

                    if(item?.uid == FBAuth.getUid()) {  //내 게시물일 때만
                        boardDataList.add(item!!) //데이터를 받아옴
                        boardKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    }

                }
                boardKeyList.reverse()
                boardDataList.reverse()
                boardRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.boardRef.addValueEventListener(postListener)

    }
}