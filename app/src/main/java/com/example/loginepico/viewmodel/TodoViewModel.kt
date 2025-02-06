package com.example.loginepico.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginepico.data.model.Todo
import com.example.loginepico.data.repository.TodoRepository
import com.example.loginepico.utils.CameraManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository,
    private val openCamera: () -> Unit
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

    private var currentPhotoPath: String? = null
    
    private val _showCamera = MutableStateFlow(false)
    val showCamera: StateFlow<Boolean> = _showCamera

    private val _currentPhotoUri = MutableStateFlow<String?>(null)
    val currentPhotoUri: StateFlow<String?> = _currentPhotoUri

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            println("Cargando todos...")  // Debug
            repository.getTodos()
                .onSuccess { todos ->
                    println("Todos cargados exitosamente: ${todos.size} items")  // Debug
                    _todos.value = todos
                    _state.value = TodoState.Success
                }
                .onFailure { e ->
                    println("Error al cargar todos: ${e.message}")  // Debug
                    _state.value = TodoState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun showAddDialog() {
        _isDialogVisible.value = true
    }

    fun hideAddDialog() {
        _isDialogVisible.value = false
        _currentPhotoUri.value = null
        currentPhotoPath = null
    }

    fun startCamera() {
        _showCamera.value = true
    }

    fun onImageCaptured(uri: Uri) {
        _currentPhotoUri.value = uri.toString()
        _showCamera.value = false
    }

    fun addTodo(title: String, description: String) {
        viewModelScope.launch {
            _state.value = TodoState.Loading
            try {
                println("Creando todo: $title")  // Debug
                val imageFile = _currentPhotoUri.value?.let { uriString ->
                    val uri = Uri.parse(uriString)
                    println("URI de imagen: $uriString")  // Debug
                    uri.path
                }
                
                val result = repository.createTodo(
                    title = title,
                    description = description,
                    imageUrl = imageFile
                )
                result.onSuccess { todo -> 
                    println("Todo creado exitosamente: ${todo.id}")  // Debug
                    loadTodos()  // Recargar la lista
                    hideAddDialog()
                }
                result.onFailure { e ->
                    println("Error al crear todo: ${e.message}")  // Debug
                    _state.value = TodoState.Error(e.message ?: "Error al crear todo")
                }
            } catch (e: Exception) {
                println("Excepción al crear todo: ${e.message}")  // Debug
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
                repository.updateTodo(id, title, description, currentPhotoPath)
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

    fun openCamera() {
        openCamera.invoke()
    }

    // Implementar resto de métodos CRUD...
}

sealed class TodoState {
    object Loading : TodoState()
    object Success : TodoState()
    data class Error(val message: String) : TodoState()
} 