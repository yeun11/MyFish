package com.example.myfish.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.api.FishRankResponse
import com.example.myfish.api.retrofit_interface
import com.example.myfish.databinding.ActivitySearchByPhotoBinding
import com.example.myfish.utils.FBAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*



class SearchByPhotoActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchByPhotoBinding

    private val fishNameList = ArrayList<String>() //물고기 순위 리스트

    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99
    val REQUEST_TAKE_PHOTO = 1
    lateinit var turnAround : Animation
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_by_photo)


        //직접 촬영하기
        binding.takePictureBtn.setOnClickListener{

            callCamera()

        }

        //갤러리에서 가져오기
        binding.getImageBtn.setOnClickListener {

            getAlbum()

        }

        //투명 배경을 눌렀을 때
        binding.transparentBg1.setOnClickListener {
            finish()
        }
        binding.transparentBg2.setOnClickListener {
            finish()
        }

    }


    //서버에 물고기 이미지 업로드
    private fun uploadImage(imageUri: Uri) {
        val inputStream = applicationContext.contentResolver.openInputStream(imageUri)
        val file = File(applicationContext.cacheDir, applicationContext.contentResolver.getFileName(imageUri))
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()

        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val gson = GsonBuilder().setLenient().create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://cb64-34-126-70-206.ngrok.io") // 서버의 베이스 URL을 설정해주세요
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService: retrofit_interface = retrofit.create(retrofit_interface::class.java)

        val call: Call<FishRankResponse> = apiService.getFishRank(body)

        call.enqueue(object : Callback<FishRankResponse> {
            override fun onResponse(call: Call<FishRankResponse>, response: Response<FishRankResponse>) {
                if (response.isSuccessful) { //서버에서 결과를 잘 받았을 때

                    //결과 데이터 받기
                    val prediction1 = response.body()?.prediction1
                    fishNameList.add(prediction1!!)
                    val prediction2 = response.body()?.prediction2
                    fishNameList.add(prediction2!!)
                    val prediction3 = response.body()?.prediction3
                    fishNameList.add(prediction3!!)
                    val prediction4 = response.body()?.prediction4
                    fishNameList.add(prediction4!!)
                    val prediction5 = response.body()?.prediction5
                    fishNameList.add(prediction5!!)


                    //물고기 순위 결과 창으로 이동
                    val intent = Intent(this@SearchByPhotoActivity, SearchByPhotoResultActivity::class.java)
                    intent.putStringArrayListExtra("fish", fishNameList)
                    startActivity(intent)

                    finish()

                } else {
                    Toast.makeText(getApplicationContext(), "서버에 사진 전송을 실패했습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<FishRankResponse>, t: Throwable) {

            }
        })
    }

    //파일 이름 얻기
    @SuppressLint("Range")
    fun ContentResolver.getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = this.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown"
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                //직접 사진찍기
                REQUEST_TAKE_PHOTO -> {
                    Log.i("REQUEST_TAKE_PHOTO", "${Activity.RESULT_OK}" + " " + "${resultCode}");
                    if (resultCode == RESULT_OK) { //성공
                        try {
                            val uri = galleryAddPic()
                            //보이게 하기
                            binding.imageView.visibility = View.VISIBLE
                            binding.imageView.setImageURI(uri)
                            binding.searchText.visibility = View.VISIBLE

                            //로딩 애니메이션
                            binding.loadingImg.visibility = View.VISIBLE
                            turnAround = AnimationUtils.loadAnimation(this@SearchByPhotoActivity, R.anim.turn_around)
                            binding.loadingImg.startAnimation(turnAround)


                            //안 보이게 하기
                            binding.takePictureBtn.visibility = View.INVISIBLE
                            binding.getImageBtn.visibility = View.INVISIBLE

                            //서버에 사진 전송
                            uploadImage(uri!!)

                        } catch (e: Exception) {
                            Log.e("REQUEST_TAKE_PHOTO", e.toString())
                        }

                    } else { //실패
                        Toast.makeText(this@SearchByPhotoActivity, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                //갤러리에서 사진 선택하기
                STORAGE_CODE -> {
                    val uri = data?.data
                    //보이게 하기
                    binding.imageView.visibility = View.VISIBLE
                    binding.imageView.setImageURI(uri)
                    binding.searchText.visibility = View.VISIBLE

                    //로딩 애니메이션
                    binding.loadingImg.visibility = View.VISIBLE
                    turnAround = AnimationUtils.loadAnimation(this@SearchByPhotoActivity, R.anim.turn_around)
                    binding.loadingImg.startAnimation(turnAround)

                    //안 보이게 하기
                    binding.takePictureBtn.visibility = View.INVISIBLE
                    binding.getImageBtn.visibility = View.INVISIBLE

                    //서버에 사진 전송
                    uploadImage(uri!!)
                }
            }
        }
    }

    //권한 승인
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해 주세요.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        }
    }

    //권한이 있는 지 확인
    private fun checkPermission(permissions: Array<out String>, type: Int): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }

        return true
    }


    //직접 사진찍기
    private fun callCamera() {

        if (checkPermission(CAMERA, CAMERA_CODE) && checkPermission(STORAGE, STORAGE_CODE)) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent.resolveActivity(packageManager) != null) {

                var photoFile: File? = null

                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    Log.e("captureCamera Error", ex.toString())

                }
                if (photoFile != null) {
                    val providerURI =
                        FileProvider.getUriForFile(this, "com.example.myfish", photoFile)
                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)


                }
            }
        }

    }

    //사진이 저장되는 임시 파일 생성
    @Throws(IOException::class)
    private fun createImageFile(): File? { // Create an image file name
        val timeStamp: String = FBAuth.getTime2()
        val imageFileName = "JPEG_$timeStamp.jpg"
        var imageFile: File? = null
        val storageDir = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures",
            "MyFish"
        )
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        imageFile = File(storageDir, imageFileName)
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    //갤러리에 촬영한 이미지 추가
    private fun galleryAddPic(): Uri? {
        val mediaScanIntent: Intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        val f = File(currentPhotoPath)
        val contentUri: Uri = Uri.fromFile(f)
        mediaScanIntent.setData(contentUri)
        sendBroadcast(mediaScanIntent)
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
        return contentUri
    }


    //갤러리에서 이미지 가져오기
    private fun getAlbum()
    {
        if (checkPermission(STORAGE, STORAGE_CODE)) {
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, STORAGE_CODE)
        }
    }



}


