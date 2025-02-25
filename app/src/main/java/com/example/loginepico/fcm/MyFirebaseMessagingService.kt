package com.example.loginepico.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token generado: $token")
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Mensaje recibido de: ${remoteMessage.from}")
        
        // Siempre mostrar la notificación, independientemente de los datos
        val title = remoteMessage.notification?.title ?: "Nueva notificación"
        val message = remoteMessage.notification?.body ?: "Tienes un nuevo mensaje"
        
        // Mostrar notificación
        showNotification(title, message)
        
        // Log de datos si existen
        remoteMessage.data.let { data ->
            Log.d("FCM", "Datos del mensaje: $data")
            when (data["action"]) {
                "new_todo" -> {
                    val todoId = data["todoId"]
                    Log.d("FCM", "Nuevo todo recibido con ID: $todoId")
                }
                "update_todo" -> {
                    val todoId = data["todoId"]
                    Log.d("FCM", "Actualización de todo con ID: $todoId")
                }
                "delete_todo" -> {
                    val todoId = data["todoId"]
                    Log.d("FCM", "Eliminar todo con ID: $todoId")
                }
                else -> {
                    Log.d("FCM", "Acción no reconocida: ${data["action"]}")
                }
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones generales"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        
        Log.d("FCM", "Notificación mostrada - Título: $title, Mensaje: $message")
    }

    private fun sendTokenToServer(token: String) {
        // Por ahora solo logueamos el token
        Log.d("FCM", "Token recibido: $token")
        // TODO: Implementar el envío del token al servidor cuando sea necesario
    }
} 