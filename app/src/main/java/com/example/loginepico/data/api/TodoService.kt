package com.example.loginepico.data.api

import com.example.loginepico.data.model.Todo
import com.example.loginepico.data.model.CreateTodoRequest
import com.example.loginepico.data.model.UpdateTodoRequest
import com.example.loginepico.data.model.TodoResponse
import retrofit2.Response
import retrofit2.http.*

interface TodoService {
    @GET("api/todos")
    suspend fun getTodos(): Response<TodoResponse>

    @POST("api/todos")
    suspend fun createTodo(@Body request: CreateTodoRequest): Response<Todo>

    @PUT("api/todos/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Body request: UpdateTodoRequest
    ): Response<Todo>

    @DELETE("api/todos/{id}")
    suspend fun deleteTodo(@Path("id") id: String): Response<Unit>

    @GET("api/todos/search")
    suspend fun searchTodos(@Query("q") query: String): Response<TodoResponse>
} 