package com.example.myfish.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityViewAllBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class ViewAllActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewAllBinding

    private val fishList = mutableListOf<FishModel>()
    private val fishKeyList = mutableListOf<String>()
    private val bookmarkList = mutableListOf<String>()

    private var classification : String = "전체 보기"
    private var allfish : Int = 0  //전체 물고기 수

    private lateinit var fishRVAdapter : FishRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all)

        //리사이클러뷰에 어댑터 연결
        fishRVAdapter = FishRVAdapter(fishList, fishKeyList, bookmarkList)

         //물고기 하나를 클릭했을 때 그 물고기로 이동
        fishRVAdapter.itemClick = object : FishRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@ViewAllActivity, FishInformActivity::class.java)
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

        getFBFishData()
        getBookmarkData()
        setupSpinner()

    }

    override fun onResume() {

        //다른 페이지에서 다시 돌아오면 아이템을 새로 가져옴
        super.onResume()
        getFBFishData()

    }

    //물고기 과를 선택하는 스피너
    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.classification_array)
        val adapter = CustomSpinnerAdapter(this, items.toList())
        binding.spinner.adapter = adapter

        binding.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                // 어댑터에서 정의한 메서드를 통해 스피너에서 선택한 아이템의 이름을 받아온다
                classification = adapter.getItem(position)
                getFBFishData()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        })
    }

    private fun getFBFishData(){ //파이어베이스에서 물고기 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                fishList.clear()
                fishKeyList.clear()
                allfish = 0 // 전체 물고기 수를 0으로 초기화

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(FishModel::class.java)

                    if(classification.equals("전체 보기")) { //전체 보기일 때
                        fishList.add(item!!) //데이터를 받아옴
                        fishKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                        allfish += 1
                    }else if ((item!!.classification).equals(classification)) {  //물고기의 과가 현재 페이지의 과와 같은 값만 가져옴
                        fishList.add(item!!) //데이터를 받아옴
                        fishKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                        allfish += 1
                    }

                }
                fishKeyList.reverse()
                fishList.reverse()
                fishRVAdapter.notifyDataSetChanged() //새로고침

                binding.allFish.text = "전체물고기 • "+allfish.toString()

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