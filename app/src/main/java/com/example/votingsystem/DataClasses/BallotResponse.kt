package com.example.votingsystem.DataClasses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class BallotResponse(
    @SerializedName("has_voted") val hasVoted: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("positions") val positions: List<Position>? = null

)