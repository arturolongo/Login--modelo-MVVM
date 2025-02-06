package com.example.loginepico.data.model

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val completed: Boolean = false,
    val userId: String? = null,
    val createdAt: String,
    val updatedAt: String
) 