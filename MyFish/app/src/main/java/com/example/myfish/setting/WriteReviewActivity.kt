package com.example.myfish.setting

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfish.R
import com.example.myfish.auth.UserModel
import com.example.myfish.databinding.ActivityWriteReviewBinding
import com.example.myfish.store.OrderModel
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class WriteReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWriteReviewBinding

    private val orderBeforeReview = mutableListOf<OrderModel>()
    private val orderBeforeReviewKey = mutableListOf<String>()
    private val writtenReview = mutableListOf<ReviewModel>()
    private val writtenReviewOrderKey = mutableListOf<String>()

    private var nickname : String = ""


    private lateinit var orderRVAdapter : CanWriteReviewRVAdapter
    private lateinit var reviewRVAdapter : WrittenReviewRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_write_review)


        //작성 가능한 리뷰 어댑터와 연결
        orderRVAdapter = CanWriteReviewRVAdapter(orderBeforeReview)
        binding.canWriteReviewRv.adapter = orderRVAdapter
        orderRVAdapter.itemClick = object : CanWriteReviewRVAdapter.ItemClick { //게시물 하나를 클릭했을 때 그 게시물로 이동
            override fun onClick(view: View, position: Int) {
                showDialog(orderBeforeReview[position],orderBeforeReviewKey[position])

            }
        }
        binding.canWriteReviewRv.layoutManager = LinearLayoutManager(this)

        //작성한 리뷰 어댑터와 연결
        reviewRVAdapter = WrittenReviewRVAdapter(writtenReview)
        binding.writtenReviewRv.adapter = reviewRVAdapter
        binding.writtenReviewRv.layoutManager = LinearLayoutManager(this)

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //작성 가능한 리뷰 탭을 눌렀을 때
        binding.canWriteReviewTap.setOnClickListener{
            //리뷰 리사이클러뷰를 안 보이게
            binding.writtenReviewRv.visibility = View.GONE

            //리뷰 작성하는 리사이클러뷰를 보이게
            binding.canWriteReviewRv.visibility = View.VISIBLE

            binding.canWriteReviewBar.setBackgroundColor(Color.parseColor("#6C6C6C"))

            binding.writtenReviewBar.setBackgroundColor(Color.parseColor("#E9E9E9"))
        }

        //작성한 리뷰탭을 눌렀을 때
        binding.writtenReviewTap.setOnClickListener {
            //리뷰 작성하는 리사이클러뷰를 안 보이게
            binding.canWriteReviewRv.visibility = View.GONE

            //리뷰 리사이클러뷰를 보이게
            binding.writtenReviewRv.visibility = View.VISIBLE

            binding.canWriteReviewBar.setBackgroundColor(Color.parseColor("#E9E9E9"))

            binding.writtenReviewBar.setBackgroundColor(Color.parseColor("#6C6C6C"))
        }

        getUserNickname()
        getReviewData()
        getOrderData(FBAuth.getUid())

    }

    private fun getOrderData(key : String){ //회원정보로 리뷰할 수 있는 주문 가져오기

        val postListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                orderBeforeReview.clear()
                orderBeforeReviewKey.clear()

                for (dataModel in dataSnapshot.children) { //날짜 키

                    for ( data in dataModel.children) {

                        val item = data.getValue(OrderModel::class.java)
                        //배송완료 상태인 주문에 + 리뷰가 작성되어 있지 않아야 함
                        if (item?.state == 3 && !writtenReviewOrderKey.contains(data.key.toString())) { //배송완료인 것만 추가
                            orderBeforeReview.add(item!!)
                            orderBeforeReviewKey.add(data.key.toString())
                        }

                    }
                }

                orderBeforeReview.reverse()
                orderBeforeReviewKey.reverse()

                orderRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        FBRef.orderRef
            .child(key) //회원
            .addValueEventListener(postListener1)


    }

    private fun showDialog(order : OrderModel, orderKey : String){ //리뷰작성 다이얼로그
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.review_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        val alertDialog = mBuilder.show()

        val review  = alertDialog.findViewById<EditText>(R.id.reviewArea)

        var number = 0.0
        //-버튼을 눌렀을 때
        alertDialog.findViewById<TextView>(R.id.minBtn)?.setOnClickListener{

            if(number > 0) { //0보다 작아지지 않게
                number -= 0.5
                alertDialog.findViewById<TextView>(R.id.score)?.setText(number.toString())
            }

        }

        //+버튼을 눌렀을 때
        alertDialog.findViewById<TextView>(R.id.plusBtn)?.setOnClickListener{

            if(number < 5) { //5보다 커지지 않게
                number += 0.5
                alertDialog.findViewById<TextView>(R.id.score)?.setText(number.toString())
            }

        }

        //완료 버튼을 눌렀을 때
        alertDialog.findViewById<Button>(R.id.finishBtn)?.setOnClickListener {
            //리뷰가 없는 경우
            if(review?.text.toString().equals("") || review?.text.toString() == null){
                Toast.makeText(this, "리뷰를 입력해주세요", Toast.LENGTH_LONG).show()
            }else{

                //데이터베이스에 값을 넣음
                FBRef.reviewRef
                    .child(order.pdId)
                    .push()
                    .setValue(ReviewModel(order.pdId,orderKey,FBAuth.getUid(),order.name,nickname,number,review?.text.toString(),FBAuth.getTime()))

                orderBeforeReview.remove(order)
                orderBeforeReviewKey.remove(orderKey)
                orderRVAdapter.notifyDataSetChanged() //새로고침
                alertDialog.cancel()

            }


        }
    }


    private fun getUserNickname() { //유저 닉네임 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(UserModel::class.java)
                    if(item!!.uid == FBAuth.getUid()) {
                        nickname = item.nickname
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }

        }
        FBRef.userRef.addValueEventListener(postListener)

    }

    private fun getReviewData(){ //리뷰 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                writtenReview.clear()
                writtenReviewOrderKey.clear()

                for (dataModel in dataSnapshot.children) { //상품 키

                    for ( data in dataModel.children) {

                        val item = data.getValue(ReviewModel::class.java)
                        if (item?.uid == FBAuth.getUid() ) { //본인 리뷰만 추가
                            writtenReview.add(item!!)
                            writtenReviewOrderKey.add(item.orderId)
                        }

                    }
                }

                writtenReview.reverse()
                writtenReviewOrderKey.reverse()

                reviewRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        FBRef.reviewRef.addValueEventListener(postListener)


    }



}