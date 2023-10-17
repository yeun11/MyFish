package com.example.myfish.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R
import com.example.myfish.setting.ReviewModel


class ReviewRVAdapter (val items : MutableList<ReviewModel>) : RecyclerView.Adapter<ReviewRVAdapter.Viewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: ReviewRVAdapter.Viewholder, position: Int) {

        holder.bindItems(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val nickName: TextView = itemView.findViewById(R.id.nickName)
        private val review : TextView = itemView.findViewById(R.id.reviewArea)
        private val score : TextView = itemView.findViewById(R.id.score)
        private val date : TextView = itemView.findViewById(R.id.date)

        fun bindItems(item: ReviewModel) {

            nickName.text = item.nickName+"님"
            review.text = item.review
            score.text = item.score.toString()
            date.text = "• "+item.date

        }

    }

}

