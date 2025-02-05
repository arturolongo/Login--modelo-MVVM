package com.example.loginepico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.loginepico.data.api.RetroClient
import com.example.loginepico.data.repository.LoginRepositoryImpl
import com.example.loginepico.data.repository.TodoRepositoryImpl
import com.example.loginepico.ui.theme.LoginEpicoTheme
import com.example.loginepico.viewmodel.LoginViewModel
import com.example.loginepico.viewmodel.TodoViewModel
import com.example.loginepico.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear las dependencias
        val loginRepository = LoginRepositoryImpl(RetroClient.loginService)
        val loginViewModel = LoginViewModel(loginRepository)

        val todoRepository = TodoRepositoryImpl(RetroClient.todoService)
        val todoViewModel = TodoViewModel(todoRepository)

        setContent {
            LoginEpicoTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        todoViewModel = todoViewModel
                    )
                }
            }
        }
    }
}