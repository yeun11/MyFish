package com.example.myfish.utils

import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.myfish.auth.UserModel
import com.example.myfish.store.OrderModel
import com.example.myfish.store.ProductModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates



class FBAuth {


    companion object {
        private lateinit var auth : FirebaseAuth

        fun getUid() : String {

            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()
        }


        fun getTime() : String {

            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm",Locale.KOREA).format(currentDateTime)

            return dateFormat

        }

        fun getTime2() : String {

            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyyMMddHHmm",Locale.KOREA).format(currentDateTime)

            return dateFormat

        }

        fun checkCurrentUser(uid : String) : Boolean {
            return uid == getUid()
        }

        fun checkManager() : Boolean {
            return getUid() == "E072fG2hQDfONxVR1vTVhzObIOA3"
        }






    }
}