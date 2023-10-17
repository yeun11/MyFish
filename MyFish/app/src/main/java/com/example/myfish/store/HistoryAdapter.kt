package com.example.myfish.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R



class HistoryAdapter(val items : MutableList<String> ) : RecyclerView.Adapter<HistoryAdapter.Viewholder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null //삭제
    var itemClick2 : ItemClick? = null //뷰 하나 클릭

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.history_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.Viewholder, position: Int) {
        if(itemClick != null) {
            holder.historyBtn.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }
        if(itemClick2 != null) {
            holder.historyTxt.setOnClickListener { v->
                itemClick2?.onClick(v, position)
            }
        }


        holder.bindItems(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val historyTxt: TextView = itemView.findViewById(R.id.historyText) //히스토리 텍스트
        val historyBtn : ImageButton = itemView.findViewById(R.id.historyDeleteBtn) //히스토리 지우는 버튼

        fun bindItems(item: String) {

            historyTxt.text = item

        }

    }


}