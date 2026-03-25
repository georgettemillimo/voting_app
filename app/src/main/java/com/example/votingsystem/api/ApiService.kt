package com.example.votingsystem.api

import com.example.votingsystem.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService{
    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("admNo") admNo: String,
        @Field("password") password: String
    ): Response<ApiResponse>

}