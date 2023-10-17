package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.myfish.R
import com.example.myfish.databinding.ActivityStoreSearchBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.*

class StoreSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreSearchBinding
    private val historyList = mutableListOf<String>()
    private lateinit var historyAdapter: HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_search)

        binding.searchText.setOnEditorActionListener(getEditorActionListener(binding.searchBtn)) // 키보드에서 done(완료) 클릭 시 , 원하는 뷰 클릭되게 하기

        //검색하기 버튼을 눌렀을 때
        binding.searchBtn.setOnClickListener {

            saveSearchKeyword(binding.searchText.text.toString())

            val intent = Intent(this, StoreSearchResultActivity::class.java)
            intent.putExtra("searchWord", binding.searchText.text.toString())
            startActivity(intent)

        }

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //히스토리 리사이클러뷰에 어댑터 연결
        historyAdapter = HistoryAdapter(historyList)

        //히스토리 삭제
        historyAdapter.itemClick = object : HistoryAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                deleteKeyword(historyList[position])
                getFBHistoryData()
            }
        }
        //히스토리 클릭
        historyAdapter.itemClick2 = object : HistoryAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(this@StoreSearchActivity, StoreSearchResultActivity::class.java)
                intent.putExtra("searchWord", historyList[position])
                startActivity(intent)
            }
        }

        binding.historyrv.adapter = historyAdapter
        binding.historyrv.layoutManager = LinearLayoutManager(this)


        getFBHistoryData()

    }

    override fun onResume() {
        //다른 페이지에서 다시 돌아오면 아이템을 새로 가져옴
        super.onResume()
        getFBHistoryData()
    }



    //키워드 값을 히스토리에 저장
    private fun saveSearchKeyword (keyword : String) {
        val uid = FBAuth.getUid()

        FBRef.storeHistoryRef //데이터베이스에 값을 넣음
            .child(uid)
            .push()
            .setValue(History(keyword))

        historyAdapter.notifyDataSetChanged()
    }

    private fun deleteKeyword (keyword : String) { //키워드 삭제

        var key : String

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                historyList.clear()

                try {

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.child("keyword").getValue()
                    if (item != null) {
                        if(item.equals(keyword)){ //키워드가 같은 검색어만 삭제
                            key = dataModel.key.toString()
                            FBRef.storeHistoryRef.child(FBAuth.getUid()).child(key).removeValue()
                        }
                    }

                }
                historyList.reverse()
                historyAdapter.notifyDataSetChanged() //새로고침
                }catch(e : Exception) {}

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        FBRef.storeHistoryRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }

    private fun getFBHistoryData(){ //파이어베이스에서 히스토리를 받아옴
        Log.d("History : ", "yes");
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    historyList.clear()

                    for (dataModel in dataSnapshot.children) {
                            val item = dataModel.child("keyword").getValue()
                            historyList.add(item.toString()) //데이터를 받아옴
                    }
                    historyList.reverse()
                    historyAdapter.notifyDataSetChanged() //새로고침

                }catch(e : Exception) {}

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.storeHistoryRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }



    // 키보드에서 done(완료) 클릭 시 , 원하는 뷰 클릭되게 하는 메소드
    private fun getEditorActionListener(view: View): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.callOnClick()
            }
            false
        }
    }
}