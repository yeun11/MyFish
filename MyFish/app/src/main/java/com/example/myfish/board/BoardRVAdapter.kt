package com.example.myfish.board

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myfish.R
import com.example.myfish.databinding.ActivityBoardWriteBinding.bind
import com.example.myfish.utils.FBAuth

class BoardRVAdapter(val items : MutableList<BoardModel>) : RecyclerView.Adapter<BoardRVAdapter.ViewHolder>(){

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //하나의 레이아웃을 만들어 줌
        val v = LayoutInflater.from(parent.context).inflate(R.layout.board_rv_item,parent,false)
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

        private val title: TextView = itemView.findViewById(R.id.board_title)
        private val time: TextView = itemView.findViewById(R.id.board_time)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val commentcnt: TextView = itemView.findViewById(R.id.commentCnt)



        fun bindItems(item : BoardModel) {

            title.text = item.title
            time.text = item.time
            nickname.text = item.name+" •"
            commentcnt.text = item.commentCount.toString()

            Log.d("test",item.uid)

            val itemRV = itemView.findViewById<ConstraintLayout>(R.id.rvItem)

            //자기가 쓴 글 색 바꿔줌
            if(item.uid.equals(FBAuth.getUid())) {
                itemRV?.setBackgroundColor(Color.parseColor("#e7e7e7"))
            }

        }

    }

}