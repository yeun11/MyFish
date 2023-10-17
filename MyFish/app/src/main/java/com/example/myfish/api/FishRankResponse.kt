package com.example.myfish.api

import com.google.gson.annotations.SerializedName

data class FishRankResponse (
    @SerializedName("prediction1")
    val prediction1: String?,
    @SerializedName("prediction2")
    val prediction2: String?,
    @SerializedName("prediction3")
    val prediction3: String?,
    @SerializedName("prediction4")
    val prediction4: String?,
    @SerializedName("prediction5")
    val prediction5: String?
)