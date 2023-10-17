package com.example.myfish.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityOrderDetailBinding
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderDetailBinding

    private val incompleteOrderList = mutableListOf<OrderModel>()
    private val incompleteOrderKeyList = mutableListOf<String>()
    private val completeOrderList = mutableListOf<OrderModel>()
    private val completeOrderKeyList = mutableListOf<String>()

    private lateinit var incompleteOrderRVAdapter : IncompleteOrderRVAdapter
    private lateinit var completeOrderRVAdapter : CompleteOrderRVAdapter

    private lateinit var key : String //회원
    private lateinit var date : String //날짜

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)


        // OrderManageInsideActivity에서 보낸 회원 데이터 받기
        key = intent.getStringExtra("key").toString() //회원
        date = intent.getStringExtra("date").toString()  //주문일자

        binding.orderDate.text = "주문일자 : "+date

        //배송이 완료되지 않은 주문 리사이클러뷰에 어댑터 연결
        incompleteOrderRVAdapter = IncompleteOrderRVAdapter(incompleteOrderList,incompleteOrderKeyList,key,date)
        binding.incompleteOrderrv.adapter = incompleteOrderRVAdapter
        binding.incompleteOrderrv.layoutManager = LinearLayoutManager(this)

        //배송이 완료된 주문 리사이클러뷰에 어댑터 연결
        completeOrderRVAdapter = CompleteOrderRVAdapter(completeOrderList)
        binding.completeOrderrv.adapter = completeOrderRVAdapter
        binding.completeOrderrv.layoutManager = LinearLayoutManager(this)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        getOrderData(key)
        getDeliveryData(key)

    }

    private fun getOrderData(key : String){ //회원정보로 주문정보 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                incompleteOrderList.clear()
                incompleteOrderKeyList.clear()
                completeOrderList.clear()
                completeOrderKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(OrderModel::class.java)
                    if (item != null) {
                        when(item.state) {
                            0-> continue
                            1,2-> {
                                incompleteOrderList.add(item!!)
                                incompleteOrderKeyList.add(dataModel.key.toString())
                            }
                            3-> {
                                completeOrderList.add(item!!)
                                completeOrderKeyList.add(dataModel.key.toString())
                            }
                        }
                    }

                }

                incompleteOrderList.reverse()
                incompleteOrderKeyList.reverse()
                completeOrderList.reverse()
                completeOrderKeyList.reverse()

                incompleteOrderRVAdapter.notifyDataSetChanged() //새로고침
                completeOrderRVAdapter.notifyDataSetChanged() //새로고침
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

                    binding.nameArea.text = "이름 : "+dataModel!!.name
                    binding.phoneArea.text = "핸드폰 번호 : "+dataModel!!.phoneNumber
                    binding.addressArea.text = "주소 : "+dataModel!!.address

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