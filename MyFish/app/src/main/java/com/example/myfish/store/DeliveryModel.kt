package com.example.myfish.store

data class DeliveryModel (
    val name : String = "", //배송 받는 사람
    var phoneNumber : String = "", //핸드폰 번호
    var address : String = "" //배송 주소
)