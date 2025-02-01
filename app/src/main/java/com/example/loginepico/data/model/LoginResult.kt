package com.example.loginepico.data.model

sealed class LoginResult {
    data class Success(val message: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
} 