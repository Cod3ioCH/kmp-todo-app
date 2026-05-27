package com.example.todo.repository

import com.example.todo.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// StateFlow ist der reaktive Kern: Jede Änderung wird automatisch an alle Beobachter weitergeleitet.
class TodoRepository {
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()

    fun addTodo(title: String) {
        if (title.isBlank()) return
        _todos.value = _todos.value + TodoItem(title = title.trim())
    }

    fun toggleTodo(id: String) {
        _todos.value = _todos.value.map { item ->
            if (item.id == id) item.copy(isCompleted = !item.isCompleted) else item
        }
    }

    fun deleteTodo(id: String) {
        _todos.value = _todos.value.filter { it.id != id }
    }
}
