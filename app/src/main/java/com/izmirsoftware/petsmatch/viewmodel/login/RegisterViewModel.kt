package com.izmirsoftware.petsmatch.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.Status
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<Resource<Boolean>>()
    val authState: LiveData<Resource<Boolean>> get() = _authState

    fun signUp(email: String, password: String, confirmPassword: String) = viewModelScope.launch {
        if (password == confirmPassword) {
            _authState.value = Resource(Status.LOADING, null, null)
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification()
                } else {
                    _authState.value = Resource(Status.ERROR, null, task.exception?.localizedMessage)
                }
            }
        } else {
            _authState.value = Resource(Status.ERROR, null, "Passwords do not match")
        }
    }

    private fun sendEmailVerification() {
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = Resource(Status.SUCCESS, true, "Verification email sent")
                firebaseAuth.signOut()
            } else {
                _authState.value = Resource(Status.ERROR, null, task.exception?.localizedMessage)
            }
        }
    }
}
