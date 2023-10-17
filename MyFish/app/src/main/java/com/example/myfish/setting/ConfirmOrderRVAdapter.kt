package com.example.myfish.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R


class ConfirmOrderRVAdapter  (val items : MutableList<String> ) : RecyclerView.Adapter<ConfirmOrderRVAdapter.Viewholder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmOrderRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.orderlist_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: ConfirmOrderRVAdapter.Viewholder, position: Int) {
        if(itemClick != null) {
            holder.itemView.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val date: TextView = itemView.findViewById(R.id.orderDate)

        fun bindItems(item: String) {

            date.text = item

        }

    }


}