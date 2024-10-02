package com.slapshotapps.dragonshockey.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


interface AuthenticationManager{
    suspend fun authenticateUserAnonymously() : Boolean
}

class AuthenticationManagerImp(private val auth: FirebaseAuth) : AuthenticationManager{


    override suspend fun authenticateUserAnonymously()= suspendCancellableCoroutine { cont ->
        if (auth.currentUser != null) {
            cont.isActive.takeIf { true }?.run { cont.resume(true) }
        } else {
            auth.signInAnonymously().addOnCompleteListener {
                cont.isActive.takeIf { true }?.run { cont.resume(it.isSuccessful) }
            }
        }
    }
}