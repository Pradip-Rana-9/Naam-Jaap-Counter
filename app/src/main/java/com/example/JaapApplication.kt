package com.example

import android.app.Application
import android.util.Log
import com.example.data.database.AppDatabase
import com.example.data.firebase.FirebaseConfig
import com.example.data.repository.JaapRepository

class JaapApplication : Application() {

    lateinit var repository: JaapRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        val db = AppDatabase.getDatabase(this)
        repository = JaapRepository(db.jaapDao(), this)

        try {
            FirebaseConfig.initialize(this)
            Log.d("JaapApplication", "FirebaseApp initialized via FirebaseConfig")
        } catch (e: Throwable) {
            Log.e("JaapApplication", "Failed to initialize Firebase: ${e.message}", e)
        }
    }

    companion object {
        lateinit var instance: JaapApplication
            private set
    }
}
