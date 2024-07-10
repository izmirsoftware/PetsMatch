package com.izmirsoftware.petsmatch.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.model.UserModel
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val currentUserId = auth.currentUser?.uid.toString()

    private var _reservationCount = MutableLiveData<Int>()
    val reservationCount: LiveData<Int>
        get() = _reservationCount

    private var _resetPasswordMessage = MutableLiveData<Resource<Boolean>>()
    val resetPasswordMessage: LiveData<Resource<Boolean>>
        get() = _resetPasswordMessage

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _userInfoMessage = MutableLiveData<Resource<Boolean>>()
    val userInfoMessage: LiveData<Resource<Boolean>>
        get() = _userInfoMessage


    private var _exitMessage = MutableLiveData<Resource<Boolean>>()
    val exitMessage: LiveData<Resource<Boolean>>
        get() = _exitMessage

    init {
        getUserData()
    }

    private fun getUserData() = viewModelScope.launch {
        _userInfoMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _userInfoMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _userInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}