package com.example.myfish.store

import java.io.Serializable

data class WishlistModel (

    val pdId : String = "",
    val name : String = "",
    val costPerPiece : Int = 0,
    val quantity: Int = 0, //수량
    var state : Int = 0 //상태 0 : 체크가 되지 않은 상태, 1 : 체크된 상태

        ) : Serializable