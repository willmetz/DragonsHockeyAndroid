package com.slapshotapps.dragonshockey.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


sealed interface AdminAuthenticationState {
    data object Error : AdminAuthenticationState
    data object Success : AdminAuthenticationState
}

interface AuthenticationManager{
    suspend fun authenticateUserAnonymously() : Boolean
    fun isUserAdmin(): Boolean
    suspend fun authenticateUser(email: String, password: String): AdminAuthenticationState
}

class AuthenticationManagerImp(private val auth: FirebaseAuth) : AuthenticationManager{


    override suspend fun authenticateUserAnonymously()= suspendCancellableCoroutine { cont ->
        if (auth.currentUser != null) {
            cont.isActive.takeIf { it }?.run { cont.resume(true) }
        } else {
            auth.signInAnonymously().addOnCompleteListener {
                cont.isActive.takeIf { true }?.run { cont.resume(it.isSuccessful) }
            }
        }
    }

    override fun isUserAdmin() = auth.currentUser?.isAnonymous == false

    override suspend fun authenticateUser(email: String, password: String ) : AdminAuthenticationState =
        suspendCancellableCoroutine{cont ->
            if(auth.currentUser?.isAnonymous == false){
                cont.isActive.takeIf { it }?.run { cont.resume(AdminAuthenticationState.Success) }
            }else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { authResult ->

                    cont.isActive.takeIf { it }?.run { cont.resume(
                        when(authResult.isSuccessful){
                            true -> AdminAuthenticationState.Success
                            false ->  AdminAuthenticationState.Error
                        }
                    ) }
                    authResult.isSuccessful
                }
            }

    }
}