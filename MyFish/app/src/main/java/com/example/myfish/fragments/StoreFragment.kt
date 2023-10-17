package com.example.myfish.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myfish.R
import com.example.myfish.auth.IntroActivity
import com.example.myfish.board.BoardModel
import com.example.myfish.board.BoardRVAdapter
import com.example.myfish.board.BoardinsideActivity
import com.example.myfish.bookmark.BookmarkModel
import com.example.myfish.databinding.FragmentHomeBinding
import com.example.myfish.databinding.FragmentStoreBinding
import com.example.myfish.store.*
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class StoreFragment : Fragment() {

    private lateinit var binding : FragmentStoreBinding

    private val productList = mutableListOf<ProductModel>()
    private val productKeyList = mutableListOf<String>()
    private val bookmarkList = mutableListOf<String>()

    private lateinit var storeRVAdapter : StoreRVAdapter

    private var numBanner = 3 // 배너 갯수
    private var currentPosition = Int.MAX_VALUE / 2
    private var myHandler = MyHandler()
    private val intervalTime = 1500.toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store, container, false)


        //배너 설정
        binding.viewPagerBanner.adapter = ViewPagerAdapter(getBannerList())
        binding.viewPagerBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerBanner.setCurrentItem(currentPosition, false) // 현재 위치를 지정
        binding.textViewTotalBanner.text = numBanner.toString()

        // 현재 몇번째 배너인지 보여주는 숫자를 변경함
        binding.viewPagerBanner.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewCurrentBanner.text = "${(position % 3) + 1}"
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        // 뷰페이저 움직이는 중
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }

        //어댑터 연결
        storeRVAdapter = StoreRVAdapter(productList, productKeyList, bookmarkList)

         //상품 하나를 클릭했을 때 그 상품으로 이동
        storeRVAdapter.itemClick = object : StoreRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                val intent = Intent(context, ProductInsideActivity::class.java)
                intent.putExtra("key", productKeyList[position])
                startActivity(intent)
            }
        }

        binding.storerv.adapter = storeRVAdapter
        binding.storerv.layoutManager = GridLayoutManager(context,2)

        //카테고리 버튼을 눌렀을 때
        binding.category0.setOnClickListener{
            clickCategory(0)
        }

        binding.category1.setOnClickListener{
            clickCategory(1)
        }

        binding.category2.setOnClickListener{
            clickCategory(2)
        }

        binding.category3.setOnClickListener{
            clickCategory(3)
        }

        binding.category4.setOnClickListener{
            clickCategory(4)
        }

        binding.category5.setOnClickListener{
            clickCategory(5)
        }

        binding.category6.setOnClickListener{
            clickCategory(6)
        }

        binding.category7.setOnClickListener{
            clickCategory(7)
        }

        binding.category8.setOnClickListener{
            clickCategory(8)
        }

        binding.category9.setOnClickListener{
            clickCategory(9)
        }


        //검색 버튼을 눌렀을 때
        binding.searchArea.setOnClickListener{

            val intent = Intent(context, StoreSearchActivity::class.java)
            startActivity(intent)

        }




        binding.homeTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_storeFragment_to_homeFragment)

        }

        binding.talkTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_storeFragment_to_talkFragment)

        }

        binding.favoriteTap.setOnClickListener{

            it.findNavController().navigate(R.id.action_storeFragment_to_bookmarkFragment)

        }

        getFBProductData()
        getBookmarkData()

        return binding.root

    }

    override fun onResume() {

        //다른 페이지에서 다시 돌아오면 아이템을 새로 가져옴
        super.onResume()
        getFBProductData()


        //다시 스크롤 시작
        super.onResume()
        autoScrollStart(intervalTime)
    }

    // 다른 페이지로 떠나있는 동안 스크롤이 동작할 필요는 없음. 정지
    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0) // 이거 안하면 핸들러가 1개, 2개, 3개 ... n개 만큼 계속 늘어남
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime 만큼 반복해서 핸들러를 실행하게 함
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                binding.viewPagerBanner.setCurrentItem(++currentPosition, true) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 이어서 한다.
            }
        }
    }

    private fun getBannerList(): ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
    }

    private fun getFBProductData(){ //파이어베이스에서 상품 데이터를 받아옴

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                productList.clear()
                productKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(ProductModel::class.java)
                    productList.add(item!!) //데이터를 받아옴
                    productKeyList.add(dataModel.key.toString()) //키 값을 받아옴

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

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }



    private fun clickCategory(num : Int) {
        val intent = Intent(context, CategoryInsideActivity::class.java)
        intent.putExtra("name", num)
        startActivity(intent)
    }

}