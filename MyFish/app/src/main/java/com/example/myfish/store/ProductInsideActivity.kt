package com.example.myfish.store

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.board.BoardModel
import com.example.myfish.board.BoardWriteActivity
import com.example.myfish.bookmark.BookmarkModel
import com.example.myfish.databinding.ActivityProductInsideBinding
import com.example.myfish.setting.ReviewModel
import com.example.myfish.setting.WrittenReviewRVAdapter
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.DecimalFormat

class ProductInsideActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProductInsideBinding

    private val bookmarkList = mutableListOf<String>()
    private val reviewList = mutableListOf<ReviewModel>()

    private lateinit var key : String //상품 키 값
    private lateinit var pdName : String
    private var pdPrice = 0

    private lateinit var reviewRVAdapter : ReviewRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_inside)

        // StoreFragment에서 보낸 상품 데이터 받기
        key = intent.getStringExtra("key").toString()
        getProductData(key)
        getImageData(key)
        getBookmarkData()
        getReviewData(key)
        chkManager()

        //작성한 리뷰 어댑터와 연결
        reviewRVAdapter = ReviewRVAdapter(reviewList)
        binding.reviewRv.adapter = reviewRVAdapter
        binding.reviewRv.layoutManager = LinearLayoutManager(this)

        //상품 정보 탭을 눌렀을 때
        binding.informTap.setOnClickListener{

            //상품 정보가 보이게
            binding.pdDetail.visibility = View.VISIBLE

            //리뷰가 안 보이게
            binding.reviewRv.visibility = View.GONE

            binding.informBar.setBackgroundColor(Color.parseColor("#6C6C6C"))

            binding.reviewBar.setBackgroundColor(Color.parseColor("#E9E9E9"))
        }

        //리뷰 탭을 눌렀을 때
        binding.reviewTap.setOnClickListener {

            //리뷰가 보이게
            binding.reviewRv.visibility = View.VISIBLE

            //상품 정보가 안 보이게
            binding.pdDetail.visibility = View.GONE

            binding.reviewBar.setBackgroundColor(Color.parseColor("#6C6C6C"))

            binding.informBar.setBackgroundColor(Color.parseColor("#E9E9E9"))

        }

        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //구매하기 버튼 눌렀을 때
        binding.buyBtn.setOnClickListener {
            val intent = Intent(this, ClickBuyBtnActivity::class.java)
            intent.putExtra("key", key)
            intent.putExtra("name", pdName)
            intent.putExtra("price", pdPrice)
            startActivity(intent)
        }

        binding.wishlistBtn.setOnClickListener{
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
        }

        //찜 버튼을 눌렀을 때
        binding.favoriteBtn.setOnClickListener {
            if(bookmarkList.contains(key)){ //북마크에 있는 경우
                binding.favoriteBtn.setImageResource(R.drawable.bottom_favorite_skyblue)

                //북마크에서 제거
                FBRef.bookmarkRef
                    .child(FBAuth.getUid())
                    .child(key)
                    .removeValue()

            }else { //북마크에 없는 경우

                binding.favoriteBtn.setImageResource(R.drawable.bottom_favorite_skyblue_fill)

                //북마크에 추가
                FBRef.bookmarkRef
                    .child(FBAuth.getUid())
                    .child(key)
                    .setValue(BookmarkModel(true))
            }
        }

    }

    private fun getImageData(key : String){ //상품 이미지 불러오기

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.productImg

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때

                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)

            } else { //이미지가 없을 때
                binding.productImg.isVisible = false
            }
        })


    }

    private fun getProductData(key : String){ //상품 데이터 불러오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try{

                    val dataModel = dataSnapshot.getValue(ProductModel::class.java)

                    binding.category.text = "전체상품 > " + dataModel!!.category
                    binding.pdName.text = dataModel!!.name
                    pdName = dataModel!!.name
                    pdPrice = dataModel!!.price
                    binding.pdPrice.text = dataModel!!.price.toString()+"원"
                    binding.pdDetail.text = dataModel!!.content


                } catch (e : Exception){

                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.productRef.child(key).addValueEventListener(postListener)



    }

    private fun chkManager() {
        if(FBAuth.checkManager()){
            binding.wishlistBtn.visibility = View.GONE
            binding.bottomArea.visibility = View.GONE
        }

    }


    private fun getBookmarkData(){ //파이어베이스에서 북마크 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkList.clear()

                for (dataModel in dataSnapshot.children) {

                    bookmarkList.add(dataModel.key.toString()) //키 값을 받아옴

                }

                bookmarkList.reverse()

                //북마크 초기화
                if(bookmarkList.contains(key)){ //북마크에 있는 경우
                    binding.favoriteBtn.setImageResource(R.drawable.bottom_favorite_skyblue_fill)

                }else { //북마크에 없는 경우

                    binding.favoriteBtn.setImageResource(R.drawable.bottom_favorite_skyblue)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }

    private fun getReviewData(key : String){ //리뷰 가져오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                reviewList.clear()

                for (dataModel in dataSnapshot.children) { //상품 키

                    for ( data in dataModel.children) {

                        val item = data.getValue(ReviewModel::class.java)
                        if (item?.pdId == key ) { //상품키와 같은 리뷰만 추가
                            reviewList.add(item!!)
                        }

                    }
                }

                reviewList.reverse()

                reviewRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        FBRef.reviewRef.addValueEventListener(postListener)


    }


}