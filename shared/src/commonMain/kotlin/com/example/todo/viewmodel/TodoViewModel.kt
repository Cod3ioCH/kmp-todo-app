package com.example.todo.viewmodel

import com.example.todo.model.TodoItem
import com.example.todo.repository.TodoRepository
import kotlinx.coroutines.flow.StateFlow

// Dieser ViewModel wird direkt von Android genutzt (via collectAsState in Compose).
// iOS nutzt stattdessen den TodoStore (iosMain), der die Callbacks bridge.
class TodoViewModel(private val repository: TodoRepository = TodoRepository()) {
    val todos: StateFlow<List<TodoItem>> = repository.todos

    fun addTodo(title: String) = repository.addTodo(title)
    fun toggleTodo(id: String) = repository.toggleTodo(id)
    fun deleteTodo(id: String) = repository.deleteTodo(id)
}
