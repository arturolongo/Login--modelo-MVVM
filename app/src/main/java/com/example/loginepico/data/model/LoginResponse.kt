package com.example.loginepico.data.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null  // Opcional porque solo viene en caso de éxito
)