package com.example.loginepico.data.repository

import com.example.loginepico.data.model.Todo

interface TodoRepository {
    suspend fun getTodos(): Result<List<Todo>>
    suspend fun createTodo(title: String, description: String, imageUrl: String?): Result<Todo>
    suspend fun updateTodo(id: String, title: String, description: String, imageUrl: String?): Result<Todo>
    suspend fun deleteTodo(id: String): Result<Boolean>
    suspend fun searchTodos(query: String): Result<List<Todo>>
} 