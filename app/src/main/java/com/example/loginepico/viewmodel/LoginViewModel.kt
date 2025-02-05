package com.example.loginepico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginepico.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode

    fun toggleMode() {
        _isRegisterMode.value = !_isRegisterMode.value
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                loginRepository.register(email, password, name)
                    .onSuccess {
                        _loginState.value = LoginState.Success("Registro exitoso")
                    }
                    .onFailure { exception ->
                        _loginState.value = LoginState.Error(exception.message ?: "Error desconocido")
                    }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                loginRepository.login(email, password)
                    .onSuccess {
                        _loginState.value = LoginState.Success("Login exitoso")
                    }
                    .onFailure { exception ->
                        _loginState.value = LoginState.Error(exception.message ?: "Error desconocido")
                    }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
} 