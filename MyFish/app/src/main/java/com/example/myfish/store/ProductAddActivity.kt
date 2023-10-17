package com.example.myfish.store

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.auth.IntroActivity
import com.example.myfish.board.BoardModel
import com.example.myfish.databinding.ActivityProductAddBinding
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ProductAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductAddBinding

    private var isImageUpload = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_add)

        //추가 버튼을 눌렀을 때
        binding.addBtn.setOnClickListener{

            val name = binding.nameArea.text.toString() //이름
            val category = binding.spinner.getSelectedItem().toString()   //카테고리
            val content = binding.contentArea.text.toString() //상품 설명
            val price = binding.priceArea.text.toString() //가격

            val key = FBRef.productRef.push().key.toString()

            FBRef.productRef //데이터베이스에 값을 넣음
                .child(key)
                .setValue(ProductModel(name,category,content,price.toInt()))

            if(isImageUpload == true) {
                imageUpload(key) //이미지 이름을 문서의 key값으로 해서 이미지 정보를 찾기 쉽게 함
            }

            finish()

        }

        //뒤로가기 버튼
        binding.backButton.setOnClickListener{

            finish()

        }

        //이미지 추가
        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }

        setupSpinner()
        setupSpinnerHandler()
    }

    //카테고리 스피너
    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        binding.spinner.adapter = adapter
    }

    private fun setupSpinnerHandler() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    //이미지를 파이어베이스에 업로드
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