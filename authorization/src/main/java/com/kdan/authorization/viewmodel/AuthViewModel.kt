package com.kdan.authorization.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    val auth: FirebaseAuth = Firebase.auth
    val isUserLoggedIn
        get() = auth.currentUser != null
    var email = ""
    var password = ""
    val messageCodes = mutableListOf<Int>()
    val showDialog = mutableStateOf(false)

    fun logOut() = run { auth.signOut() }

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: "null"
    }
}