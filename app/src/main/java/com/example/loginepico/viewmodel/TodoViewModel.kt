package com.example.loginepico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginepico.data.model.Todo
import com.example.loginepico.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TodoState>(TodoState.Loading)
    val state: StateFlow<TodoState> = _state

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isEditDialogVisible = MutableStateFlow(false)
    val isEditDialogVisible: StateFlow<Boolean> = _isEditDialogVisible

    private val _selectedTodo = MutableStateFlow<Todo?>(null)
    val selectedTodo: StateFlow<Todo?> = _selectedTodo

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            repository.getTodos()
                .onSuccess { todos ->
                    _todos.value = todos
                    _state.value = TodoState.Success
                }
                .onFailure { e ->
                    _state.value = TodoState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun showAddDialog() {
        _isDialogVisible.value = true
    }

    fun hideAddDialog() {
        _isDialogVisible.value = false
    }

    fun addTodo(title: String, description: String) {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            try {
                repository.createTodo(title, description)
                    .onSuccess { 
                        loadTodos() // Recargar la lista
                        hideAddDialog()
                    }
                    .onFailure { e ->
                        _state.value = TodoState.Error(e.message ?: "Error al crear todo")
                    }
            } catch (e: Exception) {
                _state.value = TodoState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            searchTodos(query)
        } else {
            loadTodos()
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            try {
                repository.deleteTodo(id)
                    .onSuccess { 
                        loadTodos() // Recargar la lista
                    }
                    .onFailure { e ->
                        _state.value = TodoState.Error(e.message ?: "Error al eliminar nota")
                    }
            } catch (e: Exception) {
                _state.value = TodoState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun showEditDialog(todo: Todo) {
        _selectedTodo.value = todo
        _isEditDialogVisible.value = true
    }

    fun hideEditDialog() {
        _selectedTodo.value = null
        _isEditDialogVisible.value = false
    }

    fun updateTodo(id: String, title: String, description: String) {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            try {
                repository.updateTodo(id, title, description)
                    .onSuccess { 
                        loadTodos() // Recargar la lista
                        hideEditDialog()
                    }
                    .onFailure { e ->
                        _state.value = TodoState.Error(e.message ?: "Error al actualizar nota")
                    }
            } catch (e: Exception) {
                _state.value = TodoState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun searchTodos(query: String) {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            try {
                repository.searchTodos(query)
                    .onSuccess { todos ->
                        _todos.value = todos
                        _state.value = TodoState.Success
                    }
                    .onFailure { e ->
                        _state.value = TodoState.Error(e.message ?: "Error al buscar notas")
                    }
            } catch (e: Exception) {
                _state.value = TodoState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // Implementar resto de métodos CRUD...
}

sealed class TodoState {
    object Loading : TodoState()
    object Success : TodoState()
    data class Error(val message: String) : TodoState()
} 