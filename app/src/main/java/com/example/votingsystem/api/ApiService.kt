package com.example.votingsystem.api


import android.annotation.SuppressLint
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable

interface ApiService {

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LoginRequest(
    val admNo: String,
    val password: String
)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class User(
    val id: Int,
    val admNo: String,
    val fullname: String
)