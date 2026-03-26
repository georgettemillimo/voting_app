package com.example.votingsystem.model

import com.example.votingsystem.api.LoginRequest
import com.example.votingsystem.api.LoginResponse
import com.example.votingsystem.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthRepository{
    private val api = RetrofitClient.authApi

    suspend fun login(admNo: String, password: String): Result<LoginResponse>{
        return withContext(Dispatchers.IO){

            try {
                val response = api.login(LoginRequest(admNo, password))
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Login failed"))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }
}

