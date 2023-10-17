package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.board.BoardModel
import com.example.myfish.board.BoardinsideActivity
import com.example.myfish.databinding.ActivityBoardinsideBinding
import com.example.myfish.databinding.ActivityCategoryInsideBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CategoryInsideActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityCategoryInsideBinding

    private val productList = mutableListOf<ProductModel>()
    private val productKeyList = mutableListOf<String>()
    private val bookmarkList = mutableListOf<String>()

    private lateinit var storeRVAdapter : StoreRVAdapter

    private var num : Int = 0 //카테고리 번호

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_inside)

        //카테고리 받아오기
        if (intent.hasExtra("name")) {
            num = intent.getIntExtra("name",0)

        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        //리사이클러뷰에 어댑터 연결
        storeRVAdapter = StoreRVAdapter(productList,productKeyList,bookmarkList)

            //상품 하나를 클릭했을 때 그 상품으로 이동
        storeRVAdapter.itemClick = object : StoreRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@CategoryInsideActivity, ProductInsideActivity::class.java)
                intent.putExtra("key", productKeyList[position])
                startActivity(intent)
            }
        }
        binding.storerv.adapter = storeRVAdapter
        binding.storerv.layoutManager = GridLayoutManager(this,2)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        getFBProductData(num)
        getBookmarkData()

    }

    private fun getFBProductData(num : Int){ //파이어베이스에서 상품 데이터를 받아옴
        var categoryName = "category"

        when(num) { //선택한 카테고리
            0-> categoryName = "어항"
            1-> categoryName = "조명"
            2-> categoryName = "사료"
            3-> categoryName = "여과기"
            4-> categoryName = "히터/냉각"
            5-> categoryName = "여과재"
            6-> categoryName = "바닥재"
            7-> categoryName = "청소용품"
            8-> categoryName = "부화통"
            9-> categoryName = "기타"
        }

        binding.categoryTitle.setText(categoryName) //큰 제목을 카테고리 이름으로 바꿔주기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                productList.clear()
                productKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(ProductModel::class.java)

                    if((item!!.category).equals(categoryName)) { //카테고리에 맞는 상품
                        productList.add(item!!) //상품 데이터를 받아옴
                        productKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    }

                }
                productKeyList.reverse()
                productList.reverse()
                storeRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.productRef.addValueEventListener(postListener)

    }

    private fun getBookmarkData(){ //파이어베이스에서 북마크 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkList.clear()

                for (dataModel in dataSnapshot.children) {

                    //val item = dataModel.getValue(BookmarkModel::class.java)
                    bookmarkList.add(dataModel.key.toString()) //키 값을 받아옴

                }

                bookmarkList.reverse()

                storeRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }


}