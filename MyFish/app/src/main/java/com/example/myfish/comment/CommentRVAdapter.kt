package com.example.myfish.comment

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R

class CommentRVAdapter (val items : MutableList<CommentModel>) : RecyclerView.Adapter<CommentRVAdapter.ViewHolder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //하나의 레이아웃을 만들어 줌
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_rv_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int { //아이템 개수가 몇 개 인지
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(itemClick != null) {
            holder.itemView.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position])


    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val nickname : TextView = itemView.findViewById(R.id.nickname)
        private val title : TextView = itemView.findViewById(R.id.titleArea)
        private val time : TextView = itemView.findViewById(R.id.timeArea)

        fun bindItems(item : CommentModel) {

            nickname!!.text = item.name+" •"
            title!!.text = item.commentTitle
            time!!.text = item.commentCreatedTime


        }

    }

}