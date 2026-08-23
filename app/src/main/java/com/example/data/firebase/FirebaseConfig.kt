package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {

    const val APPLICATION_ID = "1:743617160895:android:bb7b8652873eada74bc80a"
    const val API_KEY = "AIzaSyD1GOGlb4nn9RwyFnseQgk8cjgr6T6logI"
    const val PROJECT_ID = "naam-jaap-9"
    const val DATABASE_URL = "https://naam-jaap-9-default-rtdb.firebaseio.com"
    const val STORAGE_BUCKET = "naam-jaap-9.firebasestorage.app"
    const val GCM_SENDER_ID = "743617160895"

    fun initialize(context: Context): FirebaseApp? {
        return try {
            val existingApps = FirebaseApp.getApps(context)
            if (existingApps.isNotEmpty()) {
                Log.d("FirebaseConfig", "FirebaseApp already initialized: ${existingApps.first().name}")
                return FirebaseApp.getInstance()
            }

            val options = FirebaseOptions.Builder()
                .setApplicationId(APPLICATION_ID)
                .setApiKey(API_KEY)
                .setProjectId(PROJECT_ID)
                .setDatabaseUrl(DATABASE_URL)
                .setStorageBucket(STORAGE_BUCKET)
                .setGcmSenderId(GCM_SENDER_ID)
                .build()

            val app = FirebaseApp.initializeApp(context.applicationContext, options)
            Log.d("FirebaseConfig", "FirebaseApp successfully initialized with explicit FirebaseOptions")
            app
        } catch (e: Exception) {
            Log.w("FirebaseConfig", "Explicit init failed, trying default init: ${e.message}")
            try {
                FirebaseApp.initializeApp(context.applicationContext)
            } catch (t: Throwable) {
                Log.e("FirebaseConfig", "Fatal Firebase initialization error: ${t.message}", t)
                null
            }
        }
    }

    fun getAuth(context: Context? = null): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.w("FirebaseConfig", "FirebaseAuth.getInstance() failed, attempting re-init: ${e.message}")
            if (context != null) {
                initialize(context)
                try {
                    FirebaseAuth.getInstance()
                } catch (t: Throwable) {
                    Log.e("FirebaseConfig", "FirebaseAuth retry failed: ${t.message}", t)
                    null
                }
            } else {
                null
            }
        }
    }

    fun getFirestore(context: Context? = null): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.w("FirebaseConfig", "FirebaseFirestore.getInstance() failed, attempting re-init: ${e.message}")
            if (context != null) {
                initialize(context)
                try {
                    FirebaseFirestore.getInstance()
                } catch (t: Throwable) {
                    Log.e("FirebaseConfig", "FirebaseFirestore retry failed: ${t.message}", t)
                    null
                }
            } else {
                null
            }
        }
    }
}
