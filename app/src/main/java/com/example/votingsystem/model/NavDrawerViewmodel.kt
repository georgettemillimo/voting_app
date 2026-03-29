package com.example.votingsystem.model

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.votingsystem.api.AuthRepository
import com.example.votingsystem.api.RetrofitClient
import com.example.votingsystem.util.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavDrawerViewmodel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
    private val apiService = RetrofitClient.authApi
    private val tokenManager = TokenManager(application)

    private val _username = mutableStateOf("Username")
    val username: State<String> = _username

    private val _electionTitle = mutableStateOf("Election Title")
    val electionTitle: State<String> = _electionTitle

    fun loadUserData() {
        viewModelScope.launch {
            try {
                // Get user profile
                tokenManager.getAuthHeader()?.let { authHeader ->
                    val response = apiService.getProfile(authHeader)
                    if (response.isSuccessful) {
                        response.body()?.let { profile ->
                            _username.value = profile.fullname
                        }
                    }
                }

                // Get election title
                val electionResponse = apiService.getElection()
                if (electionResponse.isSuccessful) {
                    electionResponse.body()?.let { election ->
                        _electionTitle.value = election.title
                    }
                }

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutEvent.emit(Unit)
        }
    }
}
