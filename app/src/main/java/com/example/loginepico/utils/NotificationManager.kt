package com.example.loginepico.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

object NotificationManager {
    fun requestNotificationPermission(context: Context, onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            when {
                ContextCompat.checkSelfPermission(context, permission) == 
                    PackageManager.PERMISSION_GRANTED -> {
                    Log.d("FCM", "Permiso de notificaciones ya concedido")
                    onResult(true)
                }
                else -> {
                    Log.d("FCM", "Solicitando permiso de notificaciones")
                    // Necesitamos solicitar el permiso desde una Activity
                    if (context is ComponentActivity) {
                        context.registerForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) { isGranted: Boolean ->
                            Log.d("FCM", "Resultado permiso notificaciones: $isGranted")
                            onResult(isGranted)
                        }.launch(permission)
                    } else {
                        Log.e("FCM", "Contexto no es una Activity")
                        onResult(false)
                    }
                }
            }
        } else {
            Log.d("FCM", "No se requiere permiso de notificaciones en esta versión")
            onResult(true)
        }
    }

    fun getFirebaseToken(onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        Log.e("FCM_TOKEN", "Solicitando token de Firebase...")
        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.e("FCM_TOKEN", "===========================================")
                        Log.e("FCM_TOKEN", "COPIAR ESTE TOKEN PARA FIREBASE CONSOLE:")
                        Log.e("FCM_TOKEN", token)
                        Log.e("FCM_TOKEN", "===========================================")
                        onSuccess(token)
                    } else {
                        Log.e("FCM_TOKEN", "Error al obtener token", task.exception)
                        onError(task.exception ?: Exception("Error desconocido"))
                    }
                }
        } catch (e: Exception) {
            Log.e("FCM_TOKEN", "Error al solicitar token", e)
            onError(e)
        }
    }
} 