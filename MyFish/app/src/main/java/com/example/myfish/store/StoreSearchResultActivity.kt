package com.example.myfish.store


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityStoreSearchResultBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class StoreSearchResultActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityStoreSearchResultBinding

    private val productList = mutableListOf<ProductModel>()
    private val productKeyList = mutableListOf<String>()
    private val bookmarkList = mutableListOf<String>()

    private lateinit var storeRVAdapter : StoreRVAdapter

    private var word : String = "" //검색어

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_search_result)

        //검색어 받아오기
        if (intent.hasExtra("searchWord")) {
             word = intent.getStringExtra("searchWord").toString()

        } else {
            Toast.makeText(this, "전달된 검색어가 없습니다", Toast.LENGTH_SHORT).show()
        }

        //리사이클러뷰에 어댑터 연결
        storeRVAdapter = StoreRVAdapter(productList,productKeyList, bookmarkList)

        //상품 하나를 클릭했을 때 그 상품으로 이동
        storeRVAdapter.itemClick = object : StoreRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(this@StoreSearchResultActivity, ProductInsideActivity::class.java)
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

        //검색하기 버튼 눌렀을 떄
        binding.search.setOnClickListener{

            finish() //다시 검색화면으로 이동

        }

        getFBProductData(word)
        getBookmarkData()

    }

    private fun getFBProductData(word: String){ //파이어베이스에서 상품 데이터를 받아옴


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                productList.clear()
                productKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(ProductModel::class.java)

                    if((item!!.name).contains(word)) { //검색어를 포함하면
                        binding.noSearchResult.isVisible = false

                        productList.add(item!!) //데이터를 받아옴
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

    private fun getBookmarkData(){ //파이어베이스에서 상품 데이터를 받아옴

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