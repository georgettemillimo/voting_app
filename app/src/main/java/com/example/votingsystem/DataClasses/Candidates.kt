package com.example.votingsystem.DataClasses

data class Candidate(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val fullname: String,
    val photo: String,
    val platform: String
)