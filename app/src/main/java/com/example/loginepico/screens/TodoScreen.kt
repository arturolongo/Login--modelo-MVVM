package com.example.loginepico.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loginepico.viewmodel.TodoViewModel
import com.example.loginepico.viewmodel.TodoState
import com.example.loginepico.data.model.Todo

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isAddDialogVisible by viewModel.isDialogVisible.collectAsStateWithLifecycle()
    val isEditDialogVisible by viewModel.isEditDialogVisible.collectAsStateWithLifecycle()
    val selectedTodo by viewModel.selectedTodo.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Pendientes",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Buscar por título") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, "Buscar") }
        )

        when (state) {
            is TodoState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is TodoState.Error -> {
                Text(
                    text = (state as TodoState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is TodoState.Success -> {
                LazyColumn {
                    items(todos) { todo ->
                        TodoItem(
                            todo = todo,
                            onEdit = { viewModel.showEditDialog(todo) },
                            onDelete = { viewModel.deleteTodo(todo.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.showAddDialog() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, "Agregar nota")
        }
    }

    if (isAddDialogVisible) {
        AddTodoDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { title, description ->
                viewModel.addTodo(title, description)
            }
        )
    }

    if (isEditDialogVisible && selectedTodo != null) {
        EditTodoDialog(
            todo = selectedTodo!!,
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { title, description ->
                viewModel.updateTodo(selectedTodo!!.id, title, description)
            }
        )
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar")
                }
            }
        }
    }
}

@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nota") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description)
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditTodoDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var description by remember { mutableStateOf(todo.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Nota") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 