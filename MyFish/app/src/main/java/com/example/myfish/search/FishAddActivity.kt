package com.example.myfish.search

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.databinding.ActivityFishAddBinding
import com.example.myfish.store.ProductModel
import com.example.myfish.utils.FBRef
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class FishAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFishAddBinding

    private var isImageUpload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_fish_add)

        //추가 버튼을 눌렀을 때
        binding.addBtn.setOnClickListener{

            val name = binding.nameArea.text.toString() //이름
            val classification = binding.classificationArea.text.toString() //과
            val fishInform = binding.fishInformArea.text.toString() //물고기 정보가 담긴 물고기

            val key = FBRef.fishRef.push().key.toString()

            FBRef.fishRef //데이터베이스에 값을 넣음
                .child(key)
                .setValue(FishModel(name,classification,fishInform))

            if(isImageUpload == true) {
                imageUpload(key) //이미지 이름을 문서의 key값으로 해서 이미지 정보를 찾기 쉽게 함
            }

            finish()

        }

        //뒤로가기 버튼을 눌렀을 때
        binding.backButton.setOnClickListener{

            finish()

        }

        //이미지 업로드 버튼
        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }
    }

    private fun imageUpload(key : String) {

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key+".png") //이미지 이름을 문서의 key값으로 설정

        // Get the data from an ImageView as bytes
        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }

    }

    //이미지 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 100) {
            binding.imageArea.setImageURI(data?.data)
        }
    }
}