package com.example.myfish.setting

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.auth.UserModel
import com.example.myfish.store.OrderModel
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CanWriteReviewRVAdapter (val items : MutableList<OrderModel>) : RecyclerView.Adapter<CanWriteReviewRVAdapter.Viewholder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanWriteReviewRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.canwritereview_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: CanWriteReviewRVAdapter.Viewholder, position: Int) {

        if(itemClick != null) {
            holder.writeBtn.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }


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
        val writeBtn : TextView = itemView.findViewById(R.id.writeBtn)
        val pdImg : ImageView = itemView.findViewById(R.id.pdImg)

        fun bindItems(item: OrderModel) {

            name.text = item.name
            quantity.text = item.quantity.toString()+"개"
            price.text = item.costPerPiece.toString()+"원"



        }

    }

}

