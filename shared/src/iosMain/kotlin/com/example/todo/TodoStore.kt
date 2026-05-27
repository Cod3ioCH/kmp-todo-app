package com.example.todo

import com.example.todo.model.TodoItem
import com.example.todo.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// iOS kann Kotlin Coroutines/Flows nicht direkt nutzen.
// TodoStore ist eine iOS-seitige Bridge: Es wickelt den StateFlow in ein Callback-Pattern.
// Swift erstellt eine Instanz, übergibt einen Closure und erhält Updates via onUpdate().
class TodoStore(private val onUpdate: (List<TodoItem>) -> Unit) {
    private val repository = TodoRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        repository.todos
            .onEach { todos -> onUpdate(todos) }
            .launchIn(scope)
    }

    fun addTodo(title: String) = repository.addTodo(title)
    fun toggleTodo(id: String) = repository.toggleTodo(id)
    fun deleteTodo(id: String) = repository.deleteTodo(id)

    fun dispose() = scope.cancel()
}
