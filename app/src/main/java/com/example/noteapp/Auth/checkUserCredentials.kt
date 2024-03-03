package com.example.noteapp.Auth

import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun signInWithEmailAndPassword(email: String?, password: String?): Boolean {
    return suspendCoroutine { continuation ->
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            // Если электронная почта или пароль пусты или равны null, вернуть false
            continuation.resume(false)
        } else {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                }
        }
    }
}
