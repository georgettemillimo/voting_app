package com.example.votingsystem.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.votingsystem.api.AuthRepository
import com.example.votingsystem.api.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application){

    private val repository = AuthRepository(application)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(admNo: String, password: String){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(admNo,password).onSuccess {
                _loginState.value = LoginState.Success(it.user)
            }.onFailure {
                _loginState.value = LoginState.Error(it.message ?: "Unknown Error")
            }

        }
    }

}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
