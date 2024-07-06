package com.izmirsoftware.petsmatch.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.izmirsoftware.petsmatch.model.UserModel
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.Status
import com.izmirsoftware.petsmatch.util.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableLiveData<Resource<Boolean>>()
    val authState: LiveData<Resource<Boolean>> get() = _authState

    private val _forgotPassword = MutableLiveData<Resource<Boolean>>()
    val forgotPassword: LiveData<Resource<Boolean>> get() = _forgotPassword

    private val _verificationEmailSent = MutableLiveData<Resource<Boolean>>()
    val verificationEmailSent: LiveData<Resource<Boolean>> get() = _verificationEmailSent

    private val userToken = MutableLiveData<Resource<String>>()

    fun getUser() = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()
    init {
        getToken()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = Resource.loading(true)
        firebaseRepo.login(email, password)
            .addOnCompleteListener { task ->
                _authState.value = Resource.loading(false)
                if (task.isSuccessful) {
                    _authState.value = Resource.success(true)
                    updateUserToken(task.result?.user?.uid ?: "")
                } else {
                    _authState.value = Resource.error(task.exception?.localizedMessage ?: "Login failed", false)
                }
            }
    }

    fun forgotPassword(email: String) = viewModelScope.launch {
        _forgotPassword.value = Resource.loading(true)
        firebaseRepo.forgotPassword(email)
            .addOnCompleteListener { task ->
                _forgotPassword.value = Resource.loading(false)
                if (task.isSuccessful) {
                    _forgotPassword.value = Resource.success(true)
                } else {
                    _forgotPassword.value = Resource.error(task.exception?.localizedMessage ?: "Password reset failed", false)
                }
            }
    }
    fun asdasd(){

    }

    fun sendVerificationEmail() = viewModelScope.launch {
        _verificationEmailSent.value = Resource.loading(true)
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            _verificationEmailSent.value = Resource.loading(false)
            if (task.isSuccessful) {
                _verificationEmailSent.value = Resource.success(true)
            } else {
                _verificationEmailSent.value = Resource.error(task.exception?.localizedMessage ?: "Failed to send verification email", false)
            }
        }
    }

    fun signInWithGoogle(idToken: String?) {
        _authState.value = Resource.loading(null)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                user?.let {
                    checkIfUserExists(it.uid, it.email ?: "")
                }
            } else {
                _authState.value = Resource.error("Error: Please try again", null)
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userToken.value = Resource.success(task.result ?: "")
            } else {
                userToken.value = Resource.error("", null)
            }
        }
    }

    private fun updateUserToken(currentUserId: String) {
        userToken.value?.data?.let { token ->
            val tokenMap = hashMapOf<String, Any?>(
                "token" to token
            )
            firebaseRepo.updateUserData(currentUserId, tokenMap)
        }
    }

    private fun checkIfUserExists(userId: String, email: String) = viewModelScope.launch {
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { document ->
                val user = document?.toUserModel()
                if (user?.userId != null) {
                    _authState.value = Resource.success(true)
                } else {
                    createUser(userId, email)
                }
            }
            .addOnFailureListener {
                createUser(userId, email)
            }
    }

    private fun createUser(userId: String, email: String) = viewModelScope.launch {
        val tempUsername = email.substringBefore("@")
        val user = makeUser(userId, tempUsername, email, userToken.value?.data ?: "")
        firebaseRepo.addUserToFirestore(user)
            .addOnSuccessListener {
                updateDisplayName(tempUsername)
            }
            .addOnFailureListener { e ->
                _authState.value = Resource.error(e.localizedMessage ?: "Error: Please try again later", null)
            }
    }

    private fun makeUser(userId: String, userName: String, email: String, token: String): UserModel {
        return UserModel(
            userId = userId,
            username = userName,
            email = email,
            token = token
        )
    }

    private fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build()

        firebaseAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = Resource.success(true)
            } else {
                _authState.value = Resource.error(task.exception?.localizedMessage ?: "Error: Please try again later", null)
            }
        }
    }
}

