package com.example.loginepico.data.model

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val userId: String,
    val createdAt: String,
    val updatedAt: String
) 