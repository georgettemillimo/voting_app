package com.example.votingsystem.model

import android.content.Context
import com.example.votingsystem.api.LoginRequest
import com.example.votingsystem.api.LoginResponse
import com.example.votingsystem.api.RetrofitClient
import com.example.votingsystem.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthRepository(private val context: Context){
    private val api = RetrofitClient.authApi
    private val tokenManager = TokenManager(context)


    suspend fun login(admNo: String, password: String): Result<LoginResponse>{
        return withContext(Dispatchers.IO){

            try {
                val response = api.login(LoginRequest(admNo, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        tokenManager.saveToken(loginResponse.token)
                        Result.success(loginResponse)
                    }?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error!! Try Again"))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Optional: Call server logout
                tokenManager.getAuthHeader()?.let { authHeader ->
                    api.logout(authHeader)
                }
            } catch (e: Exception) {
                // Ignore server errors
            }

            // Always clear local token
            tokenManager.clearToken()
            Result.success(Unit)
        }
    }
}


