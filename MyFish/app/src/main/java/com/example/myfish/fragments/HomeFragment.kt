package com.example.myfish.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.myfish.R
import com.example.myfish.databinding.FragmentHomeBinding
import com.example.myfish.search.*
import com.example.myfish.setting.ReviewModel
import com.example.myfish.store.OrderModel
import com.example.myfish.store.ProductInsideActivity
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef



class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //게시판으로 이동
        binding.talkTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_homeFragment_to_talkFragment)

        }

        //찜으로 이동
        binding.favoriteTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)

        }

        //스토어로 이동
        binding.storeTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_homeFragment_to_storeFragment)

        }

        //물고기 추가
        binding.fishAddBtn.setOnClickListener {

            val intent = Intent(context, FishAddActivity::class.java)
            startActivity(intent)

        }
        //물고기 정보 추가
        binding.fishInformAddBtn.setOnClickListener {

            val intent = Intent(context, FishInformAddActivity::class.java)
            startActivity(intent)

        }

        //사진으로 검색을 눌렀을 때
        binding.searchByPhoto.setOnClickListener {

            val intent = Intent(context, SearchByPhotoActivity::class.java)
            startActivity(intent)

        }

        //이름으로 검색을 눌렀을 때
        binding.searchByName.setOnClickListener {

            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)

        }

        //전체보기를 눌렀을 때
        binding.viewAll.setOnClickListener {

            val intent = Intent(context, ViewAllActivity::class.java)
            startActivity(intent)

        }

        return binding.root
    }



}