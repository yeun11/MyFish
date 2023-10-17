package com.example.myfish.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myfish.R
import com.example.myfish.databinding.ActivityFishInformAddBinding
import com.example.myfish.store.ProductModel
import com.example.myfish.utils.FBRef

class FishInformAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFishInformAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fish_inform_add)


        //추가 버튼을 눌렀을 때
        binding.addBtn.setOnClickListener{

            val name = binding.nameArea.text.toString() //이름
            val classification = binding.classificationArea.text.toString() //과
            val water = binding.waterQualityArea.text.toString() //적정수질
            val temperature = binding.temperatureArea.text.toString() //적정수온
            val feature = binding.fishFeatureArea.text.toString()  //특징
            val together = binding.raiseTogetherArea.text.toString() //합사 정보

            val key = FBRef.fishInformRef.push().key.toString()

            FBRef.fishInformRef //데이터베이스에 값을 넣음
                .child(key)
                .setValue(FishInformModel(name,classification,water, temperature, feature, together))

            finish()

        }

        binding.backButton.setOnClickListener{

            finish()

        }

    }
}