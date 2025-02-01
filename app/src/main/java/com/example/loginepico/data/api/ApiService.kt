package com.example.loginepico.data.api

import com.example.loginepico.data.model.LoginRequest
import com.example.loginepico.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}