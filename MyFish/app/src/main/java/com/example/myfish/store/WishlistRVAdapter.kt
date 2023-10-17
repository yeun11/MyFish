package com.example.myfish.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.example.myfish.utils.FBAuth
import com.example.myfish.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class WishlistRVAdapter (val items : MutableList<WishlistModel>, val keys : MutableList<String>) : RecyclerView.Adapter<WishlistRVAdapter.Viewholder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.wishlist_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: WishlistRVAdapter.Viewholder, position: Int) {

        holder.bindItems(items[position],keys[position])


        //이미지 받아오기, 연결
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
        private val price : TextView = itemView.findViewById(R.id.pdPrice)
        private val quantity : TextView = itemView.findViewById(R.id.pdQuantity)
        val pdImg : ImageView = itemView.findViewById(R.id.pdImg)
        val check : CheckBox = itemView.findViewById(R.id.checkBox)
        val clear : ImageButton = itemView.findViewById(R.id.clearBtn)

        fun bindItems(item: WishlistModel, key : String) {

            name.text = item.name
            price.text = item.costPerPiece.toString()+"원 /"
            quantity.text = item.quantity.toString()+"개"

            if(item.state == 1) {
                check.setChecked(true)
            }else {
                check.setChecked(false)
            }

            //체크박스를 눌렀을 때
            check.setOnClickListener {

                //체크가 되어있을 때
                if(item.state == 1) {
                    FBRef.wishlistRef   //wishlist
                        .child(FBAuth.getUid())    //사용자uid
                        .child(key)
                        .setValue(
                            WishlistModel(item.pdId, item.name, item.costPerPiece, item.quantity, 0) // 상품 id, 상품 이름, 개당 가격, 수량, 상태
                        )

                }else{ //체크가 되어있지 않을 때
                    FBRef.wishlistRef   //wishlist
                        .child(FBAuth.getUid())    //사용자uid
                        .child(key)
                        .setValue(
                            WishlistModel(item.pdId, item.name, item.costPerPiece, item.quantity, 1) // 상품 id, 상품 이름, 개당 가격, 수량, 상태
                        )
                }

            }


            //삭제 버튼을 눌렀을 때
            clear.setOnClickListener{
                //값 제거
                FBRef.wishlistRef   //wishlist
                    .child(FBAuth.getUid())    //사용자uid
                    .child(key)
                    .removeValue()
            }

        }

    }



}