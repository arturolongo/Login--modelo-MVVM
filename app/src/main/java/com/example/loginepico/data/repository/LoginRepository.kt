package com.example.loginepico.data.repository

interface LoginRepository {
    suspend fun login(username: String, password: String): Result<Boolean>
    suspend fun register(email: String, password: String, name: String): Result<Boolean>
} 