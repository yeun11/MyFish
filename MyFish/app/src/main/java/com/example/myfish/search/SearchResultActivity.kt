package com.example.myfish.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivitySearchResultBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchResultBinding

    private val fishList = mutableListOf<FishModel>()
    private val fishKeyList = mutableListOf<String>()
    private val bookmarkList = mutableListOf<String>()

    private lateinit var fishRVAdapter : FishRVAdapter

    private var word : String = "" //검색어

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result)

        //검색어 받아오기
        if (intent.hasExtra("searchWord")) {
            word = intent.getStringExtra("searchWord").toString()

        } else {
            Toast.makeText(this, "전달된 검색어가 없습니다", Toast.LENGTH_SHORT).show()
        }

        //리사이클러뷰에 어댑터 연결
        fishRVAdapter = FishRVAdapter(fishList, fishKeyList, bookmarkList)

        //물고기 하나를 클릭했을 때 그 물고기로 이동
        fishRVAdapter.itemClick = object : FishRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@SearchResultActivity, FishInformActivity::class.java)
                intent.putExtra("key", fishKeyList[position])
                startActivity(intent)
            }
        }
        binding.fishRv.adapter = fishRVAdapter
        binding.fishRv.layoutManager = GridLayoutManager(this,2)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //검색하기를 눌렀을 때
        binding.search.setOnClickListener {

            finish() //다시 검색화면으로 돌아가기

        }

        getFBFishData()
        getBookmarkData()

    }

    private fun getFBFishData(){ //파이어베이스에서 물고기 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                fishList.clear()
                fishKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(FishModel::class.java)

                    if((item!!.name).contains(word)) { //검색어를 포함하면
                        binding.noSearchResult.isVisible = false

                        fishList.add(item!!) //데이터를 받아옴
                        fishKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    }

                }
                fishKeyList.reverse()
                fishList.reverse()
                fishRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.fishRef.addValueEventListener(postListener)

    }

    private fun getBookmarkData(){ //파이어베이스에서 북마크 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkList.clear()

                for (dataModel in dataSnapshot.children) {

                    bookmarkList.add(dataModel.key.toString()) //키 값을 받아옴

                }

                bookmarkList.reverse()


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }
}