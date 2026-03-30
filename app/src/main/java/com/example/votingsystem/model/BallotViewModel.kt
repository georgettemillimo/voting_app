package com.example.votingsystem.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.votingsystem.DataClasses.Position
import com.example.votingsystem.api.RetrofitClient
import com.example.votingsystem.api.VoteRequest
import com.example.votingsystem.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.toMutableMap


class BallotViewModel(application: Application): AndroidViewModel(application){

    private val apiService = RetrofitClient.authApi
    private val tokenManager = TokenManager(application)
    private val _ballotState = MutableStateFlow<BallotUiState>(BallotUiState.Loading)
    val ballotState: StateFlow<BallotUiState> = _ballotState.asStateFlow()

    private val _selectedCandidates = MutableStateFlow<Map<String, List<Int>>>(emptyMap())
    val selectedCandidates: StateFlow<Map<String, List<Int>>> = _selectedCandidates.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    fun loadBallot() {
        viewModelScope.launch {
            try {
                _ballotState.value = BallotUiState.Loading

                tokenManager.getAuthHeader()?.let { authHeader ->
                    val response = apiService.getBallot(authHeader)

                    if (response.isSuccessful) {
                        response.body()?.let { ballot ->
                            if (ballot.hasVoted) {
                                _ballotState.value = BallotUiState.AlreadyVoted(ballot.message ?: "You have already voted")
                            } else {
                                _ballotState.value = BallotUiState.Success(ballot.positions ?: emptyList())
                            }
                        }
                    } else {
                        _ballotState.value = BallotUiState.Error("Failed to load ballot")
                    }
                } ?: run {
                    _ballotState.value = BallotUiState.Error("Not authenticated")
                }

            } catch (e: Exception) {
                _ballotState.value = BallotUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleCandidate(positionSlug: String, candidateId: Int, maxVote: Int) {
        val current = _selectedCandidates.value.toMutableMap()
        val positionSelections = current.getOrDefault(positionSlug, emptyList()).toMutableList()

        if (positionSelections.contains(candidateId)) {
            // Deselect
            positionSelections.remove(candidateId)
        } else {
            // Select
            if (maxVote == 1) {
                // Single selection - replace
                positionSelections.clear()
                positionSelections.add(candidateId)
            } else {
                // Multiple selection - check limit
                if (positionSelections.size < maxVote) {
                    positionSelections.add(candidateId)
                }
            }
        }



        if (positionSelections.isEmpty()) {
            current.remove(positionSlug)
        } else {
            current[positionSlug] = positionSelections
        }

        _selectedCandidates.value = current
    }


    fun resetPosition(positionSlug: String) {
        val current = _selectedCandidates.value.toMutableMap()
        current.remove(positionSlug)
        _selectedCandidates.value = current
    }

    fun isCandidateSelected(positionSlug: String, candidateId: Int): Boolean {
        return _selectedCandidates.value[positionSlug]?.contains(candidateId) ?: false
    }




    fun submitVote() {
        viewModelScope.launch {
            try {
                _submitState.value = SubmitState.Submitting

                val votes = _selectedCandidates.value

                if (votes.isEmpty()) {
                    _submitState.value = SubmitState.Error("Please select at least one candidate")
                    return@launch
                }

                tokenManager.getAuthHeader()?.let { authHeader ->
                    // Send exactly like web: {"ec-vice-chair": [1], "ec-chair": [2]}
                    val response = apiService.submitVote(authHeader, votes)

                    if (response.isSuccessful && response.body()?.success == true) {
                        _submitState.value = SubmitState.Success("Vote submitted successfully!")
                    } else {
                        val errorMsg = response.body()?.error
                            ?: response.errorBody()?.string()
                            ?: "Failed to submit"
                        _submitState.value = SubmitState.Error(errorMsg)
                    }
                } ?: run {
                    _submitState.value = SubmitState.Error("Not authenticated")
                }

            } catch (e: Exception) {
                _submitState.value = SubmitState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = SubmitState.Idle
    }
}
    sealed class BallotUiState{
        object Loading: BallotUiState()
        data class Success(val positions: List<Position>): BallotUiState()
        data class AlreadyVoted(val message: String): BallotUiState()
        data class Error(val message: String): BallotUiState()

    }

    sealed class SubmitState{
        object Idle: SubmitState()
        object Submitting: SubmitState()
        data class Success(val message: String): SubmitState()
        data class Error(val message: String): SubmitState()

    }

