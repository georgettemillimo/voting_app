package com.example.votingsystem.DataClasses

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Candidate(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val fullname: String,
    val photo: String,
    val platform: String
)