package com.example.myfish.store

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef


class IncompleteOrderRVAdapter (val items : MutableList<OrderModel> , val orderKeyList : MutableList<String>,val key : String, val date : String) : RecyclerView.Adapter<IncompleteOrderRVAdapter.Viewholder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncompleteOrderRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.incompleteorder_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: IncompleteOrderRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position], position)


    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.pdName)
        private val quantity : TextView = itemView.findViewById(R.id.pdQuantity)
        private val state : TextView = itemView.findViewById(R.id.pdState)
        private var stateName : String = "state"
        private val shippingBtn : Button = itemView.findViewById(R.id.shippingBtn)
        private val deliveryCompleteBtn : Button = itemView.findViewById(R.id.deliveryCompleteBtn)
        var stateMap = mutableMapOf<String,Any>(Pair("state",1))


        fun bindItems(item: OrderModel, position: Int) {

            when(item.state) {
                1-> stateName = "/ 상태 : 주문완료"
                2-> stateName = "/ 상태 : 배송중"
                3-> stateName = "/ 상태 : 배송완료"
            }

            name.text = item.name
            quantity.text = item.quantity.toString()+"개"
            state.text = stateName


            shippingBtn.setOnClickListener {
                stateMap["state"] = 2 //배송중(2)

                Log.d("상태체크 :",stateMap.toString())

                FBRef.orderRef   //order
                    .child(key)    //사용자uid
                    .child(date)  //주문시간
                    .child(orderKeyList[position])
                    .updateChildren(stateMap)
            }

            deliveryCompleteBtn.setOnClickListener {
                stateMap["state"] = 3 //배송완료(3)

                FBRef.orderRef   //order
                    .child(key)    //사용자uid
                    .child(date)  //주문시간
                    .child(orderKeyList[position])
                    .updateChildren(stateMap)
            }


        }

    }


}