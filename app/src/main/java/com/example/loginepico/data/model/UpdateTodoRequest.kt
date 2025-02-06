package com.example.loginepico.data.model

data class UpdateTodoRequest(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val completed: Boolean
) 