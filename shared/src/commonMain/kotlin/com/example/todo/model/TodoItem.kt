package com.example.todo.model

import kotlin.random.Random

data class TodoItem(
    val id: String = Random.nextLong().toString(),
    val title: String,
    val isCompleted: Boolean = false
)
