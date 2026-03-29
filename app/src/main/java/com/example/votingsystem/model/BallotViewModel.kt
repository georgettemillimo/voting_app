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

    private val _selectedCandidates = MutableStateFlow<Map<Int, List<Int>>>(emptyMap())
    val selectedCandidates: StateFlow<Map<Int, List<Int>>> = _selectedCandidates.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    fun loadBallot(){
        viewModelScope.launch {
            try {
                _ballotState.value = BallotUiState.Loading
                tokenManager.getAuthHeader()?.let { authHeader ->
                    val response = apiService.getBallot(authHeader)
                    if (response.isSuccessful) {
                        response.body()?.let { ballot ->
                            if (ballot.hasVoted) {
                                _ballotState.value = BallotUiState.AlreadyVoted(ballot.message?:"You have already voted")
                            }else{
                                _ballotState.value = BallotUiState.Success(ballot.positions?: emptyList())
                            }
                        }
                    } else{
                        _ballotState.value = BallotUiState.Error("Error: Failed to load ballot")
                    }

                }?: run {
                    _ballotState.value = BallotUiState.Error("Error: Not Authenticated")
                }
            }
            catch (e: Exception){
                _ballotState.value = BallotUiState.Error("Error: ${e.message}")

            }
        }
    }

    fun toggleCandidate(positionId: Int, candidateId: Int, maxVote: Int){
        val currentSelection = _selectedCandidates.value.toMutableMap()
        val positionsSelection = currentSelection.getOrDefault(positionId, emptyList()).toMutableList()
        if (positionsSelection.contains(candidateId)){
            //Deselect
            positionsSelection.remove(candidateId)
        }
        else{
            //Select
            if (maxVote==1){

                // Single selection - replace
                positionsSelection.clear()
                positionsSelection.add(candidateId)
            }
            else{
                // Multiple selection - check limit
               if (positionsSelection.size < maxVote){
                   positionsSelection.add(candidateId)
               }
            }

        }

        currentSelection[positionId] = positionsSelection
        _selectedCandidates.value = currentSelection

    }

    fun resetPosition(positionId: Int){
        val currentSelection = _selectedCandidates.value.toMutableMap()
        currentSelection.remove(positionId)
        _selectedCandidates.value = currentSelection

    }

    fun submitVote(){
        viewModelScope.launch {
            try {
                _submitState.value = SubmitState.Submitting
                val allVotes = _selectedCandidates.value.values.flatten()

                if (allVotes.isEmpty()){
                    _submitState.value = SubmitState.Error("Please select at least one candidate")
                    return@launch
                }
                tokenManager.getAuthHeader()?.let { authHeader ->
                    val response = apiService.submitVote(authHeader, VoteRequest(allVotes))
                    if (response.isSuccessful && response.body()?.success == true){
                        _submitState.value = SubmitState.Success(response.body()?.message?:"Vote submitted successfully")

                    }
                    else{
                        _submitState.value = SubmitState.Error("Error: Failed to submit vote")
                    }

                }?: run {
                    _submitState.value = SubmitState.Error("Error: Not Authenticated")
                }


            }catch (e: Exception){
                _submitState.value = SubmitState.Error("Error: ${e.message}")
            }
        }

    }
    fun resetSubmitState(){
        _submitState.value = SubmitState.Idle

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

}