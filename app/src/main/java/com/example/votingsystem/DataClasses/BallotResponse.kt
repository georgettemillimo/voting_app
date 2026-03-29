package com.example.votingsystem.DataClasses

data class BallotResponse(
    val hasVoted: Boolean,
    val message: String? = null,
    val positions: List<Position>? = null
)