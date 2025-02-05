package com.example.loginepico.data.repository

import com.example.loginepico.data.api.LoginService
import com.example.loginepico.data.model.LoginRequest
import com.example.loginepico.data.model.RegisterRequest

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

    override suspend fun register(email: String, password: String, name: String): Result<Boolean> {
        return try {
            val request = RegisterRequest(email, password, name)
            val response = loginService.register(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Registro fallido: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 