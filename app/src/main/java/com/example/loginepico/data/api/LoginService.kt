package com.example.loginepico.data.api

import com.example.loginepico.data.model.LoginRequest
import com.example.loginepico.data.model.LoginResponse
import com.example.loginepico.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>
} 