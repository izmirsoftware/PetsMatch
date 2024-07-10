package com.izmirsoftware.petsmatch.viewmodel.profile

import android.net.Uri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject
constructor(
    private val repo : FirebaseRepoInterFace,
    private val auth : FirebaseAuth
):ViewModel(){
    private val currentUserId = auth.currentUser?.uid.toString()

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _userInfoMessage = MutableLiveData<Resource<Boolean>>()
    val userInfoMessage : LiveData<Resource<Boolean>>
        get() = _userInfoMessage

    private var _uploadMessage = MutableLiveData<Resource<Boolean>>()
    val uploadMessage : LiveData<Resource<Boolean>>
        get() = _uploadMessage

    init {
        getUserData()
    }
    private fun getUserData() = viewModelScope.launch{
        _userInfoMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _userInfoMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _userInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun updateUserData(updateMap: HashMap<String, Any?>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateUserData(currentUserId, updateMap).addOnSuccessListener {
                _uploadMessage.value = Resource.success(null)
            }.addOnFailureListener{
                _uploadMessage.value = Resource.error(it.localizedMessage,null)
            }
        }
    }
    fun uploadUserPhoto(image : Uri, key : String, map :HashMap<String, Any?>?){
        repo.uploadUserProfilePhoto(image, currentUserId, key) // kapak resmini yüklüyoruz
            .addOnSuccessListener { task ->
                task.storage.downloadUrl
                    .addOnSuccessListener { uri ->
                        if (map != null){
                            map[key] = uri.toString()
                            updateUserData(map)
                        }else{
                            val updateMap = HashMap<String, Any?>()
                            updateMap[key] = uri.toString()
                            updateUserData(updateMap)
                        }
                    }.addOnFailureListener {
                        _uploadMessage.value = Resource.error(it.localizedMessage,null)
                    }
            }.addOnFailureListener{
                _uploadMessage.value = Resource.error(it.localizedMessage,null)
            }
    }
    fun getMapIfDataChanged(oldUser: UserModel, newUser: UserModel) :HashMap<String, Any?>{
        val updateMap = HashMap<String, Any?>()
        println("user : "+oldUser)
        println("new : "+newUser)

        if (newUser.username != null && newUser.username!!.isNotEmpty() && oldUser.username != newUser.username) {
            updateMap["username"] = newUser.username!!
        }

        if (newUser.email != null && newUser.email!!.isNotEmpty() && oldUser.email != newUser.email) {
            updateMap["email"] = newUser.email!!
        }
        if (newUser.phone != null && newUser.phone!!.isNotEmpty() && oldUser.phone != newUser.phone) {
            updateMap["phone"] = newUser.phone!!
        }
        if (newUser.bio != null && newUser.bio!!.isNotEmpty() && oldUser.bio != newUser.bio) {
            updateMap["bio"] = newUser.bio!!
        }

        return updateMap
    }
    fun startLoading(){
        _uploadMessage.value = Resource.loading(null)
    }
}
