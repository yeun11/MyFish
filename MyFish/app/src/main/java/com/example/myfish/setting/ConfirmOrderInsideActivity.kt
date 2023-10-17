package com.example.myfish.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityConfirmOrderInsideBinding
import com.example.myfish.store.*
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ConfirmOrderInsideActivity : AppCompatActivity() {

    private lateinit var binding : ActivityConfirmOrderInsideBinding

    private val orderList = mutableListOf<OrderModel>()

    private lateinit var orderRVAdapter : ConfirmOrderInsideRVAdapter

    private lateinit var date : String //날짜

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_order_inside)

        date = intent.getStringExtra("date").toString()  //주문일자

        binding.orderDate.text = "주문일자  "+date

        //주문 리사이클러뷰에 어댑터 연결
        orderRVAdapter = ConfirmOrderInsideRVAdapter(orderList)
        binding.confirmOrderrv.adapter = orderRVAdapter
        binding.confirmOrderrv.layoutManager = LinearLayoutManager(this)

        //뒤로가기 버튼을 눌렀을 때
        binding.backButton.setOnClickListener{
            finish()
        }

        getOrderData(FBAuth.getUid())
        getDeliveryData(FBAuth.getUid())

    }

    private fun getOrderData(key : String){ //회원정보로 주문정보 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                orderList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(OrderModel::class.java)
                    orderList.add(item!!)

                }

                orderList.reverse()

                orderRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.orderRef
            .child(key) //회원
            .child(date) //날짜
            .addValueEventListener(postListener)

    }

    private fun getDeliveryData(key : String){ //회원정보로 배송지 정보 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try{

                    val dataModel = dataSnapshot.getValue(DeliveryModel::class.java)

                    binding.nameArea.text = dataModel!!.name
                    binding.phoneArea.text = dataModel!!.phoneNumber
                    binding.addressArea.text = dataModel!!.address

                } catch (e : Exception){

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.deliveryRef
            .child(key) //회원
            .child(date) //날짜
            .addValueEventListener(postListener)

    }
}