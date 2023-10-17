package com.example.myfish.store

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.utils.FBAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class OrderManageRVAdapter(val items : MutableList<String> ) : RecyclerView.Adapter<OrderManageRVAdapter.Viewholder>() {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderManageRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ordermanage_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: OrderManageRVAdapter.Viewholder, position: Int) {
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

        private val useruid: TextView = itemView.findViewById(R.id.userUid)

        fun bindItems(item: String) {

            useruid.text = "UID : "+item

            val itemRV = itemView.findViewById<ConstraintLayout>(R.id.rvItem)

//            //처리되지 않은 상태이면 하늘색으로 바꿔줌
//            if(FBAuth.chkIncompleteState(item)) {
//                itemRV?.setBackgroundColor(Color.parseColor("#e7e7e7"))
//            }

        }

    }

}