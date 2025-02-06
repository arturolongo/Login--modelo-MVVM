package com.example.loginepico

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var todoViewModel: TodoViewModel
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido, ahora podemos abrir la cámara
            launchCamera()
        } else {
            Toast.makeText(
                this,
                "Se necesita permiso de cámara para esta función",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val imageFile = File(externalCacheDir, "camera_photo.jpg")
            if (imageFile.exists()) {
                todoViewModel.onImageCaptured(Uri.fromFile(imageFile))
                Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: archivo no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoRepository = TodoRepositoryImpl(RetroClient.todoService)
        todoViewModel = TodoViewModel(todoRepository) {
            // Primero solicitamos el permiso
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        val loginRepository = LoginRepositoryImpl(RetroClient.loginService)
        val loginViewModel = LoginViewModel(loginRepository)

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

    private fun openCamera() {
        // Verificar si ya tenemos el permiso
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == 
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val uri = createImageUri()
        cameraLauncher.launch(uri)
    }

    private fun createImageUri(): Uri {
        val imageFile = File(externalCacheDir, "camera_photo.jpg")
        return FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )
    }
}