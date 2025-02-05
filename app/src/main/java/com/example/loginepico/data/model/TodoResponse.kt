package com.example.loginepico.data.model

data class TodoResponse(
    val success: Boolean,
    val data: List<Todo>,
    val message: String
) 