package com.example.myfish.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.bookmark.BookmarkModel
import com.example.myfish.databinding.ActivityFishInformBinding
import com.example.myfish.store.ProductModel
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FishInformActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFishInformBinding

    private val bookmarkList = mutableListOf<String>() //북마크 리스트

    private lateinit var key : String //물고기 키 값

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fish_inform)

        // 물고기 키 값 받기
        key = intent.getStringExtra("key").toString()

        getFishData(key)
        getImageData(key)
        getBookmarkData()


        //뒤로가기 버튼 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //하트 버튼을 눌렀을 때
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



    private fun getFishData(key : String){ //물고기 데이터 불러오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try{

                    val dataModel = dataSnapshot.getValue(FishModel::class.java)

                    binding.fishName.text = dataModel!!.name //물고기 이름
                    getFishInfromData(dataModel!!.fishInform) //물고기 정보가 있는 물고기 이름으로 데이터 가져오기


                } catch (e : Exception){

                }



            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.fishRef.child(key).addValueEventListener(postListener)



    }

    private fun getFishInfromData(informName : String){ //물고기 정보 데이터 불러오기

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(FishInformModel::class.java)
                    if((item!!.name).contains(informName)) { //물고기 이름을 포함하는 정보 데이터일 때
                        binding.classification.text = item.classification
                        binding.temperatureInform.text = item.temperature+"℃"
                        binding.waterInform.text = item.waterQuality
                        binding.raiseTogetherInform.text = item.raiseTogether
                        binding.feature.text = item.feature
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        FBRef.fishInformRef.addValueEventListener(postListener)


    }

    private fun getImageData(key : String){ //물고기 이미지 불러오기

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.fishImg

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때

                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)

            } else { //이미지가 없을 때
                binding.fishImg.isVisible = false
            }
        })


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
}