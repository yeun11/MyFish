package com.example.myfish.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfish.R
import com.example.myfish.databinding.FragmentBookmarkBinding
import com.example.myfish.databinding.FragmentStoreBinding
import com.example.myfish.search.FishInformActivity
import com.example.myfish.search.FishModel
import com.example.myfish.search.FishRVAdapter
import com.example.myfish.store.ProductInsideActivity
import com.example.myfish.store.ProductModel
import com.example.myfish.store.StoreRVAdapter
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class BookmarkFragment : Fragment() {

    private lateinit var binding : FragmentBookmarkBinding

    private val fishList = mutableListOf<FishModel>()
    private val fishKeyList = mutableListOf<String>()
    private val productList = mutableListOf<ProductModel>()
    private val productKeyList = mutableListOf<String>()
    private val bookmarkList = mutableListOf<String>()

    private lateinit var fishRVAdapter : FishRVAdapter
    private lateinit var storeRVAdapter : StoreRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)

        getBookmarkData()
        getFBFishData()
        getFBProductData()

        //물고기 리사이클러뷰에 어댑터 연결
        fishRVAdapter = FishRVAdapter(fishList, fishKeyList, bookmarkList)

            //물고기 하나를 클릭했을 때 그 물고기로 이동
        fishRVAdapter.itemClick = object : FishRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(context, FishInformActivity::class.java)
                intent.putExtra("key", fishKeyList[position])
                startActivity(intent)
            }
        }
        binding.fishRv.adapter = fishRVAdapter
        binding.fishRv.layoutManager = GridLayoutManager(context,2)

        //쇼핑몰 리사이클러뷰에 어댑터 연결
        storeRVAdapter = StoreRVAdapter(productList, productKeyList, bookmarkList)

            //상품 하나를 클릭했을 때 그 상품으로 이동
        storeRVAdapter.itemClick = object : StoreRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(context, ProductInsideActivity::class.java)
                intent.putExtra("key", productKeyList[position])
                startActivity(intent)
            }
        }

        binding.storeRv.adapter = storeRVAdapter
        binding.storeRv.layoutManager = GridLayoutManager(context,2)


        //물고기탭을 눌렀을 때
        binding.fishTap.setOnClickListener{
            //물고기 리사이클러뷰를 보이게
            binding.fishRv.visibility = View.VISIBLE

            //스토어 리사이클러뷰를 안 보이게
            binding.storeRv.visibility = View.GONE

            binding.fishBar.setBackgroundColor(Color.parseColor("#6C6C6C"))

            binding.shoppingBar.setBackgroundColor(Color.parseColor("#E9E9E9"))
        }

        //스토어탭을 눌렀을 때
        binding.shoppingTap.setOnClickListener {
            //리뷰 리사이클러뷰를 보이게
            binding.storeRv.visibility = View.VISIBLE

            //리뷰 작성하는 리사이클러뷰를 안 보이게
            binding.fishRv.visibility = View.GONE

            binding.shoppingBar.setBackgroundColor(Color.parseColor("#6C6C6C"))

            binding.fishBar.setBackgroundColor(Color.parseColor("#E9E9E9"))
        }


        //homefragment로 이동
        binding.homeTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)

        }

        //게시판 fragment로 이동
        binding.talkTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)

        }

        //스토어 fragment로 이동
        binding.storeTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_bookmarkFragment_to_storeFragment)

        }

        return binding.root
    }

    private fun getFBFishData(){ //파이어베이스에서 물고기 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                fishList.clear()
                fishKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    if(bookmarkList.contains(dataModel.key.toString())) { //북마크에 포함된 물고기만 추가
                        val item = dataModel.getValue(FishModel::class.java)
                        fishList.add(item!!) //데이터를 받아옴
                        fishKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    }

                }
                fishKeyList.reverse()
                fishList.reverse()
                fishRVAdapter.notifyDataSetChanged() //새로고침

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.fishRef.addValueEventListener(postListener)

    }

    private fun getFBProductData(){ //파이어베이스에서 상품 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                productList.clear()
                productKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    if(bookmarkList.contains(dataModel.key.toString())) { //북마크에 포함된 상품만 추가
                        val item = dataModel.getValue(ProductModel::class.java)
                        productList.add(item!!) //데이터를 받아옴
                        productKeyList.add(dataModel.key.toString()) //키 값을 받아옴
                    }

                }
                productKeyList.reverse()
                productList.reverse()
                storeRVAdapter.notifyDataSetChanged() //새로고침


            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.productRef.addValueEventListener(postListener)

    }


    private fun getBookmarkData(){ //파이어베이스에서 북마크 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                bookmarkList.clear()

                for (dataModel in dataSnapshot.children) {

                    bookmarkList.add(dataModel.key.toString()) //키 값을 받아옴

                }

                bookmarkList.reverse()

                //북마크에서 없어질 때 데이터 새로가져오기
                getFBProductData()
                getFBFishData()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }


}