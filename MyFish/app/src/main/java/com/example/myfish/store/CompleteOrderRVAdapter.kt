package com.example.myfish.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R

class CompleteOrderRVAdapter (val items : MutableList<OrderModel> ) : RecyclerView.Adapter<CompleteOrderRVAdapter.Viewholder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteOrderRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.completeorder_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: CompleteOrderRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])


    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.pdName)
        private val quantity : TextView = itemView.findViewById(R.id.pdQuantity)
        private val state : TextView = itemView.findViewById(R.id.pdState)
        private var stateName : String = "state"


        fun bindItems(item: OrderModel) {

            when(item.state) {
                1-> stateName = "/ 상태 : 주문완료"
                2-> stateName = "/ 상태 : 배송중"
                3-> stateName = "/ 상태 : 배송완료"
            }

            name.text = item.name
            quantity.text = item.quantity.toString()+"개"
            state.text = stateName


        }

    }


}