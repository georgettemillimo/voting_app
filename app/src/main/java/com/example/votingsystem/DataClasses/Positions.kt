package com.example.votingsystem.DataClasses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Position(
    val id: Int,
    val description: String,
    val slug: String,
    @SerializedName("max_vote") val maxVote: Int = 1,
    val instruction: String,
    val candidates: List<Candidate>
)