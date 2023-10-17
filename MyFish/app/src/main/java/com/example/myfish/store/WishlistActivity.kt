package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityWishlistBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWishlistBinding

    private val wishList = mutableListOf<WishlistModel>()
    private val wishKeyList = mutableListOf<String>()


    private lateinit var  wishlistRVAdapter: WishlistRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_wishlist)

        wishlistRVAdapter = WishlistRVAdapter(wishList,wishKeyList)
        binding.wishlistrv.adapter = wishlistRVAdapter
        binding.wishlistrv.layoutManager = GridLayoutManager(this,1)


        //뒤로가기를 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //구매하기를 눌렀을 때
        binding.buyBtn.setOnClickListener{


            if(wishList.isEmpty()) { //상품이 없으면

                Toast.makeText(this, "장바구니에 상품을 추가해주세요", Toast.LENGTH_LONG).show()

            }
            else {

                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
                finish()

            }



        }

        getWishlistData()

    }

    override fun onResume() {
        //다른페이지에서 돌아왔을 때
        super.onResume()
        getWishlistData()

    }


    private fun getWishlistData() { //장바구니 데이터 불러오기
        val postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                wishList.clear()
                wishKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(WishlistModel::class.java)
                    wishList.add(item!!) //데이터를 받아옴
                    wishKeyList.add(dataModel.key.toString()) //키 값을 받아옴

                }

                wishList.reverse()
                wishKeyList.reverse()


                wishlistRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.wishlistRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }




}