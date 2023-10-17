package com.example.myfish.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.comment.CommentModel
import com.example.myfish.databinding.ActivityClickBuyBtnBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef

class ClickBuyBtnActivity : AppCompatActivity() {

    private lateinit var binding : ActivityClickBuyBtnBinding

    private lateinit var key : String
    private lateinit var pdName : String
    private var pdPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_click_buy_btn)

        var number = 0; //수량

        // ProductInsideActivity에서 보낸 상품 데이터 받기
        key = intent.getStringExtra("key").toString()
        pdName = intent.getStringExtra("name").toString()
        pdPrice = intent.getIntExtra("price",0)


        //뒷 배경을 눌렀을 때
        binding.transparentBg.setOnClickListener{

            finish()

        }

        //장바구니 버튼을 눌렀을 때
        binding.wishBtn.setOnClickListener{

            val uid = FBAuth.getUid() //사용자


            if(number > 0) {
                FBRef.wishlistRef   //wishlist
                    .child(uid)    //사용자uid
                    .push()
                    .setValue(
                        WishlistModel(key, pdName, pdPrice, number, 0) // 상품 id, 상품 이름, 수량, 상태
                    )

                Toast.makeText(this, "장바구니에 상품을 담았어요!", Toast.LENGTH_LONG).show()

                finish()
            }else {
                Toast.makeText(this, "상품을 한 개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        //주문하기 버튼을 눌렀을 때
        binding.buyBtn.setOnClickListener{

            if(number > 0) {

                val intent = Intent(this, PaymentActivity::class.java)

                intent.putExtra("wishlist",WishlistModel(key, pdName, pdPrice, number, 0))
                startActivity(intent)

            }else {
                Toast.makeText(this, "상품을 한 개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        //수량 -버튼을 눌렀을 때
        binding.minBtn.setOnClickListener{

            if(number > 0) { //0보다 작아지지 않게
                number--
                binding.quantity.setText(number.toString())
            }

        }

        //수량 +버튼을 눌렀을 때
        binding.plusBtn.setOnClickListener{

            if(number < 10) { //10보다 커지지 않게
                number++
                binding.quantity.setText(number.toString())
            }

        }


    }
}