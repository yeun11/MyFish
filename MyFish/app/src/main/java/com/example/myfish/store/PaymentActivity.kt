package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.ActivityPaymentBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPaymentBinding

    private lateinit var wish : WishlistModel
    private val productList = mutableListOf<WishlistModel>()
    private val wishKeyList = mutableListOf<String>()
    private var totalPrice: Int = 0

    private lateinit var paymentRVAdapter : PaymentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        //리사이클러뷰에 어댑터 연결
        paymentRVAdapter = PaymentRVAdapter(productList)
        binding.paymentrv.adapter = paymentRVAdapter
        binding.paymentrv.layoutManager = GridLayoutManager(this,1)

        //데이터 받아오기
        if(intent.hasExtra("wishlist")) { // 1. ClickBuyBtn에서 보낸 상품 데이터 받기

            wish = intent.getSerializableExtra("wishlist") as WishlistModel

            getProductData1()

        }else { //2. WishlistActivity에서 넘어 왔을 때

            getProductData2()

        }

        //결제 버튼을 눌렀을 때
        binding.buyBtn.setOnClickListener{

            //값이 비어있는지 확인
            if(binding.nameArea.text.toString().isEmpty()) {

                Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()

            }
            else if(binding.phoneArea.text.toString().isEmpty()) {

                Toast.makeText(this, "휴대폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show()

            }
            else if(binding.addressArea.text.toString().isEmpty()) {

                Toast.makeText(this, "주소를 입력해주세요", Toast.LENGTH_SHORT).show()

            }else if(binding.phoneArea.text.toString().length < 11) { //휴대폰 번호가 11자리 미만일 때

                Toast.makeText(this, "휴대폰 번호 11자리를 모두 입력해주세요", Toast.LENGTH_SHORT).show()

            }else{//모든 값이 다 있을 때

                val uid = FBAuth.getUid() //사용자

                if(intent.hasExtra("wishlist")) { // 1. ClickBuyBtn에서 넘어왔을 때

                    val time = FBAuth.getTime2()

                    FBRef.orderRef   //order
                        .child(uid)    //사용자uid
                        .child(time)  //주문시간
                        .push()
                        .setValue(
                            OrderModel(wish.pdId, wish.name, wish.costPerPiece, wish.quantity, 1) // 상품 id, 상품 이름, 가격, 수량, 상태(구매완료상태 : 1)
                        )

                    //배송지 정보 추가
                    FBRef.deliveryRef
                        .child(FBAuth.getUid())
                        .child(time)
                        .setValue(
                            DeliveryModel(binding.nameArea.text.toString(), binding.phoneArea.text.toString(), binding.addressArea.text.toString())
                        )

                }else { //2. WishlistActivity에서 넘어왔을 때

                    buyProduct()

                }

                Toast.makeText(this, "구매를 완료했어요!", Toast.LENGTH_LONG).show()

                finish()
            }

        }

        //뒤로가기를 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }


    }



    private fun getProductData1() { //주문하기(ClickBuyBtnActivity)에서 넘어왔을 때


                productList.clear()
                totalPrice = 0


                totalPrice = wish!!.costPerPiece*wish.quantity
                productList.add(wish!!) //데이터를 받아옴


                binding.totalPrice.text = totalPrice.toString()+"원"
                binding.buyBtn.text = totalPrice.toString()+"원 결제하기"


                paymentRVAdapter.notifyDataSetChanged() //새로고침


    }

    private fun getProductData2() { //장바구니(WishlistActivity)에서 넘어왔을 때
        val postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                productList.clear()
                wishKeyList.clear()
                totalPrice = 0

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(WishlistModel::class.java)
                    if(item?.state == 1) {
                        productList.add(item!!) //장바구니 데이터를 받아옴
                        wishKeyList.add(dataModel.key.toString())
                        totalPrice += item!!.costPerPiece*item.quantity
                    }

                }

                productList.reverse()
                wishKeyList.reverse()

                binding.totalPrice.text = totalPrice.toString()+"원"
                binding.buyBtn.text = totalPrice.toString()+"원 결제하기"


                if(productList.isEmpty()) { //장바구니 체크 데이터가 없으면

                    finish()
                }

                paymentRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.wishlistRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }


    private fun buyProduct() { //구매하기

        val time = FBAuth.getTime2() //주문시간

        for((index,item) in productList.withIndex()) {
            //주문데이터에 추가하고
            FBRef.orderRef   //order
                .child(FBAuth.getUid())    //사용자uid
                .child(time)   //주문시간
                .push()
                .setValue(
                    OrderModel(item.pdId, item.name, item.costPerPiece, item.quantity, 1) // 상품 id, 상품 이름, 가격, 수량, 상태(구매완료상태 : 1)
                )

            //장바구니에서 삭제
            FBRef.wishlistRef   //wishlist
                .child(FBAuth.getUid())    //사용자uid
                .child(wishKeyList[index])
                .removeValue()
        }

        //배송지 정보 추가
        FBRef.deliveryRef
            .child(FBAuth.getUid())
            .child(time)
            .setValue(
                DeliveryModel(binding.nameArea.text.toString(), binding.phoneArea.text.toString(), binding.addressArea.text.toString())
            )

        wishKeyList.clear()
        productList.clear()
    }


}