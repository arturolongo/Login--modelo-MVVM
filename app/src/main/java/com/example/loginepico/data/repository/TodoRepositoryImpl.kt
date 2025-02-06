package com.example.loginepico.data.repository

import com.example.loginepico.data.api.TodoService
import com.example.loginepico.data.model.Todo
import com.example.loginepico.data.model.CreateTodoRequest
import com.example.loginepico.data.model.UpdateTodoRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class TodoRepositoryImpl(
    private val todoService: TodoService
) : TodoRepository {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000"  // URL base para el emulador
    }

    override suspend fun getTodos(): Result<List<Todo>> {
        return try {
            println("Solicitando todos al servidor...")
            val response = todoService.getTodos()
            if (response.isSuccessful) {
                val todoResponse = response.body()
                println("Respuesta del servidor: $todoResponse")
                if (todoResponse?.success == true) {
                    val todos = todoResponse.data.map { todo ->
                        todo.copy(
                            userId = todo.userId ?: "default",
                            imageUrl = todo.imageUrl?.let { url ->
                                if (url.startsWith("http")) url
                                else if (url.startsWith("/uploads")) {
                                    "$BASE_URL$url"
                                } else {
                                    "$BASE_URL/uploads/$url"
                                }
                            }
                        )
                    }
                    println("Todos procesados: ${todos.size}")
                    todos.forEach { todo ->
                        println("Todo: ${todo.title}, UserId: ${todo.userId}, Image: ${todo.imageUrl}")
                    }
                    Result.success(todos)
                } else {
                    Result.failure(Exception(todoResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al obtener todos: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("Error getting todos: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun createTodo(title: String, description: String, imageUrl: String?): Result<Todo> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val userIdBody = "default".toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageUrl?.let { path ->
                try {
                    val file = File(path)
                    if (file.exists()) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("file", file.name, requestFile)
                    } else {
                        println("File does not exist: $path")
                        null
                    }
                } catch (e: Exception) {
                    println("Error creating file part: ${e.message}")
                    null
                }
            }

            val response = todoService.createTodo(titleBody, descriptionBody, userIdBody, imagePart)
            if (response.isSuccessful) {
                val todoResponse = response.body()
                if (todoResponse?.success == true) {
                    val todo = todoResponse.data
                    Result.success(todo.copy(
                        imageUrl = todo.imageUrl?.let { url ->
                            if (url.startsWith("http")) url
                            else "$BASE_URL$url"
                        }
                    ))
                } else {
                    Result.failure(Exception(todoResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al crear todo"))
            }
        } catch (e: Exception) {
            println("Error creating todo: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateTodo(id: String, title: String, description: String, imageUrl: String?): Result<Todo> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageUrl?.let { url ->
                val file = File(url)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }

            val response = todoService.updateTodo(id, titleBody, descriptionBody, imagePart)
            if (response.isSuccessful) {
                val todoResponse = response.body()
                if (todoResponse?.success == true) {
                    val todo = todoResponse.data.copy(
                        imageUrl = todoResponse.data.imageUrl?.let { url ->
                            if (url.startsWith("http")) url
                            else "$BASE_URL$url"
                        }
                    )
                    Result.success(todo)
                } else {
                    Result.failure(Exception(todoResponse?.message ?: "Error desconocido"))
                }
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