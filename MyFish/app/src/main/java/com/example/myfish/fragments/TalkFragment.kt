package com.example.myfish.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R
import com.example.myfish.board.*
import com.example.myfish.comment.CommentModel
import com.example.myfish.databinding.FragmentBookmarkBinding
import com.example.myfish.databinding.FragmentTalkBinding
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class TalkFragment : Fragment() {

    private lateinit var binding : FragmentTalkBinding

    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private var allpost : Int = 0  //전체 게시물 수

    private lateinit var boardRVAdapter: BoardRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk, container, false)

        getFBBoardData()

        //게시판 리사이클러뷰에 어댑터 연결
        boardRVAdapter = BoardRVAdapter(boardDataList)
        boardRVAdapter.itemClick = object : BoardRVAdapter.ItemClick { //게시물 하나를 클릭했을 때 그 게시물로 이동
            override fun onClick(view: View, position: Int) {

                val intent = Intent(context, BoardinsideActivity::class.java)
                intent.putExtra("key", boardKeyList[position])
                startActivity(intent)
            }
        }
        binding.boardRView.adapter = boardRVAdapter
        binding.boardRView.layoutManager = LinearLayoutManager(context)


        //내 게시글 보기, 작성하기
        binding.floatingBtn.setOnClickListener{
            val intent = Intent(context, FloatingBtnActivity::class.java)
            startActivity(intent)
        }

        //홈으로 이동
        binding.homeTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_talkFragment_to_homeFragment)

        }

        //찜으로 이동
        binding.favoriteTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_talkFragment_to_bookmarkFragment)

        }

        //스토어로 이동
        binding.storeTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_talkFragment_to_storeFragment)

        }

        return binding.root
    }


    private fun getFBBoardData(){ //파이어베이스에서 게시판 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                boardDataList.clear()
                allpost = 0 // 전체 게시물 수를 0으로 초기화

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!) //데이터를 받아옴
                    boardKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    allpost += 1

                }
                boardKeyList.reverse()
                boardDataList.reverse()
                boardRVAdapter.notifyDataSetChanged() //새로고침
                binding.allPosts.text = "전체글 • "+allpost.toString() 

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.boardRef.addValueEventListener(postListener)

    }


}