package com.izmirsoftware.petsmatch.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.Status
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<Resource<Boolean>>()
    val authState: LiveData<Resource<Boolean>> get() = _authState

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = Resource(Status.LOADING, null, null)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = Resource(Status.SUCCESS, true, null)
            } else {
                _authState.value = Resource(Status.ERROR, null, task.exception?.localizedMessage)
            }
        }
    }

    fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }
}
