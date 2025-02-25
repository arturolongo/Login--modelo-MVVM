package com.example.loginepico

import android.app.Application
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.installations.FirebaseInstallations

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        try {
            Log.e("Firebase", "Iniciando configuración de Firebase")
            // Verificar Google Play Services
            val availability = GoogleApiAvailability.getInstance()
            val resultCode = availability.isGooglePlayServicesAvailable(this)
            if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.e("Firebase", "Google Play Services disponible")
                // Inicializar Firebase
                if (FirebaseApp.getApps(this).isEmpty()) {
                    val options = FirebaseOptions.Builder()
                        .setProjectId("loginarturito-9a2dc")
                        .setApplicationId("1:771786843734:android:14b20819939604b83afdf7")
                        .setApiKey("AIzaSyDaXc4dOh30XBodwV9WZ7lftseHkTTP3JQ")
                        .build()
                    
                    FirebaseApp.initializeApp(this, options)
                    Log.d("Firebase", "Firebase inicializado correctamente")
                }
                
                // Forzar la generación del token
                FirebaseInstallations.getInstance().id
            } else {
                Log.e("Firebase", "Google Play Services NO disponible: $resultCode")
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error al inicializar Firebase", e)
        }
    }
} 