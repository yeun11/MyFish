package com.example.myfish.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object {

        private val database = Firebase.database

        val fishRef = database.getReference("fish")
        val fishInformRef = database.getReference("fishInform")
        val userRef = database.getReference("user")
        val boardRef = database.getReference("board")
        val commentRef = database.getReference("comment")
        val productRef = database.getReference("product")
        val bookmarkRef = database.getReference("bookmark")
        val orderRef = database.getReference("order")
        val deliveryRef = database.getReference("delivery")
        val wishlistRef = database.getReference("wishlist")
        val searchHistoryRef = database.getReference("searchHistory")
        val storeHistoryRef = database.getReference("storeHistory")
        val reviewRef = database.getReference("review")
    }
}