package com.example.loginepico.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.loginepico.LoginScreen
import com.example.loginepico.screens.TodoScreen
import com.example.loginepico.viewmodel.LoginViewModel
import com.example.loginepico.viewmodel.TodoViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Todo : Screen("todo")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    todoViewModel: TodoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Todo.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Todo.route) {
            TodoScreen(viewModel = todoViewModel)
        }
    }
} 