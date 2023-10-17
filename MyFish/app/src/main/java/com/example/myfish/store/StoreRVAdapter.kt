package com.example.myfish.store

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.bookmark.BookmarkModel
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.DecimalFormat

class StoreRVAdapter(val items : MutableList<ProductModel>, val keyList : MutableList<String>, val bookmarkList : MutableList<String>) : RecyclerView.Adapter<StoreRVAdapter.Viewholder>(){

    private val manager : Boolean = FBAuth.checkManager()

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.store_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: StoreRVAdapter.Viewholder, position: Int) {
        if(itemClick != null) {
            holder.itemView.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position],position)

        if(manager) {//관리자인 경우
            holder.delete.visibility = View.VISIBLE
            holder.pdBookmark.visibility = View.INVISIBLE
        }

        //이미지 받아오기
        val storageReference = Firebase.storage.reference.child(keyList[position] + ".png")
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때
                Glide.with(holder.itemView.context)
                    .load(task.result)
                    .into(holder.pdImg)
            } else { //이미지가 없을 때

            }
        })



    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.pdName)
        private val price : TextView = itemView.findViewById(R.id.pdPrice)
        val pdImg : ImageView = itemView.findViewById(R.id.pdImg)
        val pdBookmark : ImageView = itemView.findViewById(R.id.pdBookmark)
        val delete : Button = itemView.findViewById(R.id.deleteBtn)

        //이미지 경로
        val storage = Firebase.storage
        val storageRef = storage.reference


        fun bindItems(item: ProductModel, position: Int) {

            name.text = item.name
            price.text = item.price.toString()+"원"

            val imgRef = storageRef.child(keyList[position]+".png")

            //상품삭제 버튼을 눌렀을 때
            delete.setOnClickListener{
                //값 제거
                FBRef.productRef
                    .child(keyList[position])
                    .removeValue()

                // 이미지파일 제거
                imgRef.delete().addOnSuccessListener {
                    // File deleted successfully
                }.addOnFailureListener {
                    // Uh-oh, an error occurred!
                }

            }


            if(bookmarkList.contains(keyList[position])){ //북마크에 있는 경우
                pdBookmark.setImageResource(R.drawable.bottom_favorite_skyblue_fill)

            }else { //북마크에 없는 경우

                pdBookmark.setImageResource(R.drawable.bottom_favorite_skyblue)

            }

            pdBookmark.setOnClickListener{

                if(bookmarkList.contains(keyList[position])){ //북마크에 있는 경우
                    pdBookmark.setImageResource(R.drawable.bottom_favorite_skyblue)

                    //북마크에서 제거
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(keyList[position])
                        .removeValue()



                }else { //북마크에 없는 경우
                    pdBookmark.setImageResource(R.drawable.bottom_favorite_skyblue_fill)

                    //북마크에 추가
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(keyList[position])
                        .setValue(BookmarkModel(true))


                }
            }

        }

    }



}