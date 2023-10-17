package com.example.myfish.setting

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.store.OrderModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ConfirmOrderInsideRVAdapter (val items : MutableList<OrderModel>) : RecyclerView.Adapter<ConfirmOrderInsideRVAdapter.Viewholder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmOrderInsideRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.confirmorderinside_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: ConfirmOrderInsideRVAdapter.Viewholder, position: Int) {
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

        private val name: TextView = itemView.findViewById(R.id.pdName)
        private val quantity : TextView = itemView.findViewById(R.id.pdQuantity)
        private val price : TextView = itemView.findViewById(R.id.pdPrice)
        private val state : TextView = itemView.findViewById(R.id.pdState)
        private var stateName : String = "state"
        val pdImg : ImageView = itemView.findViewById(R.id.pdImg)


        fun bindItems(item: OrderModel) {

            when(item.state) {
                1-> {
                    stateName = "주문완료"
                    state.setTextColor(Color.RED)
                }
                2-> {
                    stateName = "배송중"
                    state.setTextColor(Color.BLUE)
                }
                3-> {
                    stateName = "배송완료"
                    state.setTextColor(Color.GRAY)
                }


            }

            name.text = item.name
            quantity.text = item.quantity.toString()+"개"
            price.text = item.costPerPiece.toString()+"원"
            state.text = stateName


        }

    }


}