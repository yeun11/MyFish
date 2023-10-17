package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.FtsOptions.Order
import com.example.myfish.R
import com.example.myfish.databinding.ActivityOrderManageInsideBinding
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class OrderManageInsideActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderManageInsideBinding

    private val orderList = mutableListOf<String>() //날짜

    private lateinit var orderRVAdapter : OrderListRVAdapter

    private lateinit var key : String //회원


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_manage_inside)

        // OrderManageActivity에서 보낸 회원 데이터 받기
        key = intent.getStringExtra("key").toString()
        getOrderData(key)
        binding.userInform.text = "UID : "+key


        //주문 리사이클러뷰에 어댑터 연결
        orderRVAdapter = OrderListRVAdapter(orderList)

        orderRVAdapter.itemClick = object : OrderListRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(this@OrderManageInsideActivity, OrderDetailActivity::class.java)
                intent.putExtra("key", key)
                intent.putExtra("date",orderList[position])
                startActivity(intent)
            }
        }
        binding.orderrv.adapter = orderRVAdapter
        binding.orderrv.layoutManager = LinearLayoutManager(this)


        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

    }


    private fun getOrderData(key : String){ //회원정보로 주문정보 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                orderList.clear()

                for (dataModel in dataSnapshot.children) {

                    orderList.add(dataModel.key.toString())

                }

                orderList.reverse()

                orderRVAdapter.notifyDataSetChanged() //새로고침
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.orderRef.child(key).addValueEventListener(postListener)

    }


}