package com.example.votingsystem.api


import com.example.votingsystem.model.AuthRepository
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService{
    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(@Body request:LoginRequest): Response<LoginResponse>

}

data class LoginRequest(val admNo: String, val password: String)
data class LoginResponse(val token: String, val user: User)
data class User (val id: Int, val admNo: String, val name: String)

