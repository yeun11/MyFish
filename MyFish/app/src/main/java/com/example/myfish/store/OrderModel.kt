package com.example.myfish.store

data class OrderModel (

    val pdId : String = "",
    val name : String = "",
    val costPerPiece : Int = 0,
    val quantity: Int = 0, //수량
    var state : Int = 0 //0 : 장바구니, 1 : 주문완료(배송전), 2 : 배송중, 3 : 배송완료

        )