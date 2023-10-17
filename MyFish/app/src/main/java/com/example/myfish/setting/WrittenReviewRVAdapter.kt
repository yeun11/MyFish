package com.example.myfish.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class WrittenReviewRVAdapter (val items : MutableList<ReviewModel>) : RecyclerView.Adapter<WrittenReviewRVAdapter.Viewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WrittenReviewRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.wittenreview_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: WrittenReviewRVAdapter.Viewholder, position: Int) {

        holder.bindItems(items[position])

        //이미지 받아오기
        val storageReference = Firebase.storage.reference.child(items[position].pdId + ".png")
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때
                Glide.with(holder.itemView.context)
                    .load(task.result)
                    .into(holder.pdImg)
                holder.pdImg.clipToOutline = true //이미지를 배경에 맞게 자름
            } else { //이미지가 없을 때

            }
        })


    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val pdName: TextView = itemView.findViewById(R.id.pdName)
        private val nickName: TextView = itemView.findViewById(R.id.nickName)
        private val review : TextView = itemView.findViewById(R.id.reviewArea)
        private val score : TextView = itemView.findViewById(R.id.score)
        private val date : TextView = itemView.findViewById(R.id.date)
        var pdImg : ImageView = itemView.findViewById(R.id.pdImg)

        fun bindItems(item: ReviewModel) {

            pdName.text = item.pdName
            nickName.text = item.nickName+"님"
            review.text = item.review
            score.text = item.score.toString()
            date.text = "• "+item.date

        }

    }

}

