package com.example.loginepico.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"  // Para el emulador de Android
    // Si usas un dispositivo físico, usa tu IP local:
    // private const val BASE_URL = "http://192.168.1.XX:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val loginService: LoginService = retrofit.create(LoginService::class.java)
    val todoService: TodoService = retrofit.create(TodoService::class.java)
}