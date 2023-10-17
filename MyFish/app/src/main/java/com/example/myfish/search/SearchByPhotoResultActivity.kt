package com.example.myfish.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivitySearchByPhotoResultBinding
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SearchByPhotoResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchByPhotoResultBinding

    private var fishNameList = ArrayList<String>()
    private val fishList = mutableListOf<FishModel>()
    private val fishKeyList = mutableListOf<String>()

    private lateinit var fishRVAdapter : RankRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_by_photo_result)

        // 물고기 이름 받기
        fishNameList = intent.getStringArrayListExtra("fish")!!

        //리사이클러뷰에 어댑터 연결
        fishRVAdapter = RankRVAdapter(fishList, fishKeyList)

        //물고기 하나를 클릭했을 때 그 물고기로 이동
        fishRVAdapter.itemClick = object : RankRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@SearchByPhotoResultActivity, FishInformActivity::class.java)
                intent.putExtra("key", fishKeyList[position])
                startActivity(intent)
            }
        }
        binding.fishRv.adapter = fishRVAdapter
        binding.fishRv.layoutManager = GridLayoutManager(this,1)

        getFBFishData()

    }


    private fun getFBFishData(){ //파이어베이스에서 물고기 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                fishList.clear()
                fishKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(FishModel::class.java)

                    if(fishNameList.contains(item?.name)) { //물고기 순위 리스트에 현재 물고기가 포함되면
                        fishList.add(item!!) //데이터를 받아옴
                        fishKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    }

                }
                fishKeyList.reverse()
                fishList.reverse()
                fishRVAdapter.notifyDataSetChanged() //새로고침

                //순서 원래대로 바꿔주기
                var tmp : FishModel
                var tmp2 : String
                for((i, name) in fishNameList.withIndex() ) {
                    for((j, fish) in fishList.withIndex() ) {
                       if( fish.name == name ) {
                           tmp = fishList[i]
                           fishList[i] = fishList[j]
                           fishList[j] = tmp

                           tmp2 = fishKeyList[i]
                           fishKeyList[i] = fishKeyList[j]
                           fishKeyList[j] = tmp2
                       }

                    }
                }

                fishNameList.clear()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.fishRef.addValueEventListener(postListener)

    }
}