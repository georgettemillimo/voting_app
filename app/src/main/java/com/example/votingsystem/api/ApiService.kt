package com.example.votingsystem.api



import android.annotation.SuppressLint
import com.example.votingsystem.DataClasses.BallotResponse
import com.example.votingsystem.DataClasses.Position
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService{

    @POST("login.php")
    suspend fun login(@Body request:LoginRequest): Response<LoginResponse>

    @GET("profile.php")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @GET("election.php")
    suspend fun getElection(): Response<ElectionResponse>

    @GET("ballot.php")
    suspend fun getBallot(@Header("Authorization") authHeader: String): Response<BallotResponse>

    @POST("submit_vote.php")
    suspend fun submitVote(
        @Header("Authorization") authHeader: String,
        @Body voteRequest: VoteRequest
    ): Response<VoteResponse>

    @POST("logout.php")
    suspend fun logout(@Header("Authorization") authHeader: String): Response<LogoutResponse>


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

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ElectionResponse(
    val title: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProfileResponse(
    val id: String,
    val admNo: String,
    val fullname: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LogoutResponse(
    val message: String
)

// VoteRequest.kt
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class VoteRequest(
    val votes: List<Int> // List of candidate IDs
)

// VoteResponse.kt
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class VoteResponse(
    val success: Boolean,
    val message: String
)