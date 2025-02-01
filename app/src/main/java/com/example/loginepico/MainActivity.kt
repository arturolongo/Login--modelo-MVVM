package com.example.loginepico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.loginepico.data.api.RetroClient
import com.example.loginepico.data.repository.LoginRepositoryImpl
import com.example.loginepico.ui.theme.LoginEpicoTheme
import com.example.loginepico.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear las dependencias
        val loginRepository = LoginRepositoryImpl(RetroClient.loginService)
        val loginViewModel = LoginViewModel(loginRepository)

        setContent {
            LoginEpicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(viewModel = loginViewModel)
                }
            }
        }
    }
}