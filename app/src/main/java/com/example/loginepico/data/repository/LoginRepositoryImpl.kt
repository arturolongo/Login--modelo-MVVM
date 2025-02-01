package com.example.loginepico.data.repository

import com.example.loginepico.data.api.LoginService
import com.example.loginepico.data.model.LoginRequest

class LoginRepositoryImpl(
    private val loginService: LoginService
) : LoginRepository {
    override suspend fun login(username: String, password: String): Result<Boolean> {
        return try {
            val request = LoginRequest(username, password)
            val response = loginService.login(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Login failed: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 