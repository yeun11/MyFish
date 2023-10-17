package com.example.myfish.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.bookmark.BookmarkModel
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FishRVAdapter (val items : MutableList<FishModel>, val keyList : MutableList<String>, val bookmarkList : MutableList<String>) : RecyclerView.Adapter<FishRVAdapter.Viewholder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.fish_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: FishRVAdapter.Viewholder, position: Int) {

        if(itemClick != null) {
            holder.itemView.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position],position)


        //이미지 받아오기
        val storageReference = Firebase.storage.reference.child(keyList[position] + ".png")
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때
                Glide.with(holder.itemView.context)
                    .load(task.result)
                    .into(holder.fishImg)
            } else { //이미지가 없을 때

            }
        })



    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.fishName)
        val fishImg : ImageView = itemView.findViewById(R.id.fishImg)
        val fishBookmark : ImageView = itemView.findViewById(R.id.fishBookmark)



        fun bindItems(item: FishModel, position: Int) {

            name.text = item.name


            //처음 불러올 때
            if(bookmarkList.contains(keyList[position])){ //북마크에 있는 경우
                fishBookmark.setImageResource(R.drawable.bottom_favorite_skyblue_fill)

            }else { //북마크에 없는 경우

                fishBookmark.setImageResource(R.drawable.bottom_favorite_skyblue)

            }

            //북마크를 눌렀을 때
            fishBookmark.setOnClickListener{

                if(bookmarkList.contains(keyList[position])){ //북마크에 있는 경우
                    fishBookmark.setImageResource(R.drawable.bottom_favorite_skyblue)

                    //북마크에서 제거
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(keyList[position])
                        .removeValue()

                }else { //북마크에 없는 경우

                    fishBookmark.setImageResource(R.drawable.bottom_favorite_skyblue_fill)

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