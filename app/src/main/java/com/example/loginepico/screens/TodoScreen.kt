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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isAddDialogVisible by viewModel.isDialogVisible.collectAsStateWithLifecycle()

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

        when (state) {
            is TodoState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is TodoState.Error -> {
                Text(
                    text = (state as TodoState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is TodoState.Success -> {
                if (todos.isEmpty()) {
                    Text(
                        text = "No hay pendientes",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(todos) { todo ->
                            TodoItem(todo = todo, viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Botón flotante para agregar
        FloatingActionButton(
            onClick = { viewModel.showAddDialog() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, "Agregar nota")
        }
    }

    // Diálogo para agregar todo
    if (isAddDialogVisible) {
        AddTodoDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { title, description ->
                viewModel.addTodo(title, description)
            },
            onTakePhoto = { viewModel.openCamera() },
            currentPhotoUri = viewModel.currentPhotoUri.collectAsStateWithLifecycle().value
        )
    }

    // Diálogo de edición
    val isEditDialogVisible by viewModel.isEditDialogVisible.collectAsStateWithLifecycle()
    val selectedTodo by viewModel.selectedTodo.collectAsStateWithLifecycle()

    if (isEditDialogVisible && selectedTodo != null) {
        EditTodoDialog(
            todo = selectedTodo!!,
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { title, description ->
                viewModel.updateTodo(selectedTodo!!.id, title, description)
            },
            onTakePhoto = { viewModel.openCamera() },
            currentPhotoUri = viewModel.currentPhotoUri.collectAsStateWithLifecycle().value
        )
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    viewModel: TodoViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contenido del texto
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Botones de acción
                Row {
                    IconButton(onClick = { viewModel.showEditDialog(todo) }) {
                        Icon(Icons.Default.Edit, "Editar")
                    }
                    IconButton(onClick = { viewModel.deleteTodo(todo.id) }) {
                        Icon(Icons.Default.Delete, "Eliminar")
                    }
                }
            }

            // Imagen si existe
            if (!todo.imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = todo.imageUrl,
                        contentDescription = "Imagen adjunta",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    onTakePhoto: () -> Unit,
    currentPhotoUri: String?
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
                
                Spacer(modifier = Modifier.height(8.dp))

                // Botón para tomar foto
                Button(
                    onClick = onTakePhoto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Añadir Foto")
                }

                // Mostrar la vista previa de la foto si existe
                if (currentPhotoUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = currentPhotoUri,
                        contentDescription = "Vista previa",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
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
    onConfirm: (String, String) -> Unit,
    onTakePhoto: () -> Unit,
    currentPhotoUri: String?
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
                
                Spacer(modifier = Modifier.height(8.dp))

                // Botón para tomar foto
                Button(
                    onClick = onTakePhoto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cambiar Foto")
                }

                // Mostrar la foto actual o la nueva
                if (currentPhotoUri != null || todo.imageUrl != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = currentPhotoUri ?: todo.imageUrl,
                        contentDescription = "Vista previa",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
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