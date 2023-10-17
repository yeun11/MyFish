package com.example.myfish.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfish.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class RankRVAdapter (val items : MutableList<FishModel>, val keyList : MutableList<String>) : RecyclerView.Adapter<RankRVAdapter.Viewholder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rank_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: RankRVAdapter.Viewholder, position: Int) {
        if(itemClick != null) {
            holder.itemView.setOnClickListener { v->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position],position)



        //이미지 받아오기
        val storageReference = Firebase.storage.reference.child(keyList[position] + ".png")
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) { //이미지가 있을 때
                Glide.with(holder.itemView.context)
                    .load(task.result)
                    .into(holder.fishImg)
            } else { //이미지가 없을 때

            }
        })



    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val ranking: TextView = itemView.findViewById(R.id.rankText)
        private val name: TextView = itemView.findViewById(R.id.fishName)
        val fishImg : ImageView = itemView.findViewById(R.id.fishImg)


        fun bindItems(item: FishModel, position: Int) {

            ranking.text = (position+1).toString()+". "
            name.text = item.name //물고기 이름


        }

    }


}