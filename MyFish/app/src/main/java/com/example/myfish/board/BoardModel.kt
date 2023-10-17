package com.example.myfish.board

data class BoardModel (
    val title : String = "",
    val content : String = "",
    val uid : String = "",
    val name : String = "",
    val time : String = "",
    var commentCount : Int = 0
    )