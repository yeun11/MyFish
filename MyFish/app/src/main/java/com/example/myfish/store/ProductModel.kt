package com.example.myfish.store

data class ProductModel (
    val name : String = "", //이름
    val category: String = "", //카테고리
    var content : String = "", //상세 설명
    var price : Int = 0 //가격
)
