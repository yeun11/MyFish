package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.myfish.databinding.ActivityOrderManageBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class OrderManageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderManageBinding

    private var userKeyList = mutableListOf<String>()
    private var userKeyList1 = mutableListOf<String>() //완료되지 않은 주문이 있는 유저
    private var userKeyList2 = mutableListOf<String>() //완료된 주문만 있는 유저

    private lateinit var orderManageRVAdapter: OrderManageRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_manage)

        //리사이클러뷰 어댑터 연결
        orderManageRVAdapter = OrderManageRVAdapter(userKeyList)

         //회원 클릭했을 때 그 회원으로 이동
        orderManageRVAdapter.itemClick = object : OrderManageRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(this@OrderManageActivity, OrderManageInsideActivity::class.java)
                intent.putExtra("key", userKeyList[position])
                startActivity(intent)
            }
        }

        binding.orderManagerv.adapter = orderManageRVAdapter
        binding.orderManagerv.layoutManager = GridLayoutManager(this,1)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        getUserData()

    }

    private fun getUserData(){ //파이어베이스에서 상품 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                userKeyList.clear()
                userKeyList1.clear()
                userKeyList2.clear()


                for (dataModel in dataSnapshot.children) {
                    userKeyList.add(dataModel.key.toString()) //키 값을 받아옴

                }

                userKeyList.reverse()

               // userKeyList = userKeyList1.plus(userKeyList2) as MutableList<String>

                orderManageRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.orderRef.addValueEventListener(postListener)

    }



    fun chkIncompleteState (uid : String) : Int {
        var result = 0

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(OrderModel::class.java)
                    when (item?.state) {

                        0, 1, 2 -> { //상태가 0,1,2면 ture
                            result = item?.state!!
                            Log.d("orderchk :", item?.state.toString())
                            Log.d("orderchk re :", result.toString())
                        }
                        else -> Log.d("orderchk :", item?.state.toString())
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }


        FBRef.orderRef.child(uid).addValueEventListener(postListener)
        Log.d("orderchk : result", result.toString())


        return result
    }

}