package com.example.loginepico.data.repository

import com.example.loginepico.data.api.TodoService
import com.example.loginepico.data.model.Todo
import com.example.loginepico.data.model.CreateTodoRequest
import com.example.loginepico.data.model.UpdateTodoRequest

class TodoRepositoryImpl(
    private val todoService: TodoService
) : TodoRepository {
    override suspend fun getTodos(): Result<List<Todo>> {
        return try {
            val response = todoService.getTodos()
            if (response.isSuccessful) {
                val todoResponse = response.body()
                if (todoResponse?.success == true) {
                    Result.success(todoResponse.data)
                } else {
                    Result.failure(Exception(todoResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al obtener todos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTodo(title: String, description: String): Result<Todo> {
        return try {
            val request = CreateTodoRequest(title, description, "userId") // TODO: Obtener userId real
            val response = todoService.createTodo(request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear todo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTodo(id: String, title: String, description: String): Result<Todo> {
        return try {
            val request = UpdateTodoRequest(title, description, false)
            val response = todoService.updateTodo(id, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar todo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTodo(id: String): Result<Boolean> {
        return try {
            val response = todoService.deleteTodo(id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al eliminar todo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchTodos(query: String): Result<List<Todo>> {
        return try {
            val response = todoService.searchTodos(query)
            if (response.isSuccessful) {
                val todoResponse = response.body()
                if (todoResponse?.success == true) {
                    Result.success(todoResponse.data)
                } else {
                    Result.failure(Exception(todoResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al buscar todos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 