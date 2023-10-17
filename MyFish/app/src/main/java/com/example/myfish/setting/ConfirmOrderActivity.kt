package com.example.myfish.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityConfirmOrderBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ConfirmOrderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityConfirmOrderBinding

    private val orderList = mutableListOf<String>() //날짜
    private lateinit var confirmOrderRVAdapter : ConfirmOrderRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_confirm_order)


        //주문 확인 리사이클러뷰에 어댑터 연결
        confirmOrderRVAdapter = ConfirmOrderRVAdapter(orderList)

        confirmOrderRVAdapter.itemClick = object : ConfirmOrderRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(this@ConfirmOrderActivity, ConfirmOrderInsideActivity::class.java)
                intent.putExtra("date",orderList[position])
                startActivity(intent)
            }
        }
        binding.confirmOrderrv.adapter = confirmOrderRVAdapter
        binding.confirmOrderrv.layoutManager = LinearLayoutManager(this)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        getOrderData(FBAuth.getUid())

    }

    private fun getOrderData(key : String){ //회원정보로 주문정보 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                orderList.clear()

                for (dataModel in dataSnapshot.children) {

                    orderList.add(dataModel.key.toString())

                }

                orderList.reverse()

                confirmOrderRVAdapter.notifyDataSetChanged() //새로고침
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.orderRef.child(key).addValueEventListener(postListener)

    }

}