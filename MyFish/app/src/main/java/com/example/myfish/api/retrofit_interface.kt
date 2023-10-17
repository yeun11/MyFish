package com.example.myfish.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface retrofit_interface {

    @Multipart
    @POST("/predict")
    fun getFishRank(
        @Part imageFile : MultipartBody.Part
    ): Call<FishRankResponse>

}