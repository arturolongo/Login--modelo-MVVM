package com.example.loginepico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loginepico.viewmodel.LoginViewModel
import com.example.loginepico.viewmodel.LoginState
import com.example.loginepico.data.repository.LoginRepository
import com.example.loginepico.data.api.ApiService
import com.example.loginepico.data.model.LoginRequest
import com.example.loginepico.data.model.LoginResponse
import com.example.loginepico.data.model.LoginResult
import retrofit2.Response
import kotlin.Result

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    val loginState = viewModel.loginState.collectAsStateWithLifecycle()
    val isRegisterMode = viewModel.isRegisterMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isRegisterMode.value) "Registro" else "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isRegisterMode.value) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isRegisterMode.value) {
                    viewModel.register(email, password, name)
                } else {
                    viewModel.login(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegisterMode.value) "Registrarse" else "Iniciar Sesión")
        }

        TextButton(
            onClick = { viewModel.toggleMode() }
        ) {
            Text(if (isRegisterMode.value) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate")
        }

        // Mostrar mensaje de error si existe
        when (val state = loginState.value) {
            is LoginState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
            is LoginState.Success -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.message, color = MaterialTheme.colorScheme.primary)
            }
            else -> {}
        }
    }

    // En el estado de éxito, navega a TodoScreen
    LaunchedEffect(loginState.value) {
        if (loginState.value is LoginState.Success) {
            onLoginSuccess()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val previewViewModel = LoginViewModel(object : LoginRepository {
        override suspend fun login(username: String, password: String): Result<Boolean> {
            return Result.success(true)
        }

        override suspend fun register(email: String, password: String, name: String): Result<Boolean> {
            return Result.success(true)
        }
    })
    LoginScreen(viewModel = previewViewModel) { }
}

