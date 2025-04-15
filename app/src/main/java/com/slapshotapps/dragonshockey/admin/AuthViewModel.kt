package com.slapshotapps.dragonshockey.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.repository.AdminAuthenticationState
import com.slapshotapps.dragonshockey.repository.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface AuthEvents{
    data object OnAuthComplete : AuthEvents
    data class OnAuthError(val title: String, val msg: String) : AuthEvents
}

@HiltViewModel
class AuthViewModel @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher,
                                        private val authenticationManager: AuthenticationManager) : ViewModel() {

    private val _authEvent = MutableSharedFlow<AuthEvents>()
    val authEvent : SharedFlow<AuthEvents> = _authEvent.asSharedFlow()

    fun checkIfAuthenticated(){
        viewModelScope.launch {
            if(authenticationManager.isUserAdmin()) {
                _authEvent.emit(AuthEvents.OnAuthComplete)
            }
        }
    }

    fun onLogin( userName: String, password: String){
        //for now just checking if it is empty
        if(userName.isNotEmpty() && password.isNotEmpty()){
            viewModelScope.launch(context = dispatcher) {
                when(authenticationManager.authenticateUser(userName, password)){
                    AdminAuthenticationState.Error -> _authEvent.emit(AuthEvents.OnAuthError("Invalid Login", "Please try again"))
                    AdminAuthenticationState.Success -> _authEvent.emit(AuthEvents.OnAuthComplete)
                }
            }
        }else{
            viewModelScope.launch(context = dispatcher) {
                _authEvent.emit(AuthEvents.OnAuthError("Invalid Login", "Please Try Again"))
            }
        }
    }
}