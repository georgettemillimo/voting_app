package com.example.votingsystem.DataClasses

data class Position(
    val id: Int,
    val description: String,
    val slug: String,
    val maxVote: Int,
    val instruction: String,
    val candidates: List<Candidate>
)