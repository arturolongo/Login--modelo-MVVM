package com.example.loginepico.data.model

data class CreateTodoRequest(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val userId: String
) 