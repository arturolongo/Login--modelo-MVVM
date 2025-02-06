package com.example.loginepico.data.api

import com.example.loginepico.data.model.Todo
import com.example.loginepico.data.model.CreateTodoRequest
import com.example.loginepico.data.model.UpdateTodoRequest
import com.example.loginepico.data.model.TodoResponse
import com.example.loginepico.data.model.SingleTodoResponse
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface TodoService {
    @GET("api/todos")
    suspend fun getTodos(): Response<TodoResponse>

    @Multipart
    @POST("api/todos")
    suspend fun createTodo(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<SingleTodoResponse>

    @Multipart
    @PUT("api/todos/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<SingleTodoResponse>

    @DELETE("api/todos/{id}")
    suspend fun deleteTodo(@Path("id") id: String): Response<Unit>

    @GET("api/todos/search")
    suspend fun searchTodos(@Query("q") query: String): Response<TodoResponse>
} 