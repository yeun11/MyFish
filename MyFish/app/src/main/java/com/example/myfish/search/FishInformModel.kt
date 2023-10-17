package com.example.myfish.search

data class FishInformModel (
    val name : String = "", //물고기 이름
    val classification : String = "", //물고기 과
    val waterQuality : String = "", //적정 수질
    val temperature : String = "", //적정 온도
    val feature : String = "", //특징
    val raiseTogether : String = "" //합사 정보
)