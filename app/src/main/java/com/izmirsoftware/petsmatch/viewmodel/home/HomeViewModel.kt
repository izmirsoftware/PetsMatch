package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.izmirsoftware.petsmatch.model.Comment
import com.izmirsoftware.petsmatch.model.Location
import com.izmirsoftware.petsmatch.model.Owner
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.PetType
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.toPetModel
import com.izmirsoftware.petsmatch.util.toPetPost
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : BaseViewModel() {
    val liveDataFirebaseUser: LiveData<FirebaseUser> = MutableLiveData()

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _catPostList = MutableLiveData<List<PetPost>>()
    val catPostList: LiveData<List<PetPost>>
        get() = _catPostList

    private var _dogPostList = MutableLiveData<List<PetPost>>()
    val dogPostList: LiveData<List<PetPost>>
        get() = _dogPostList


    init {
        _firebaseMessage.value = Resource.loading(null)
        getAllUSer()
        getDogsFromFirestore()
        getCatsFromFirestore()
    }

    private fun getDogsFromFirestore() = viewModelScope.launch {
        firebaseRepo.getPetPostsByPetType(PetType.DOG,10)
            .addOnSuccessListener {
                val postList = mutableListOf<PetPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toPetPost()?.let { post ->
                        postList.add(post)
                    }
                }
                _dogPostList.value = postList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
            }
    }
    private fun getCatsFromFirestore() = viewModelScope.launch {
        firebaseRepo.getPetPostsByPetType(PetType.CAT, 10)
            .addOnSuccessListener {
                val postList = mutableListOf<PetPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toPetPost()?.let { post ->
                        postList.add(post)
                    }
                }
                _catPostList.value = postList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
            }
    }
    fun signInAnonymously() = viewModelScope.launch {
        liveDataResult.mutable.value = Resource.loading(true)
        auth.signInAnonymously()
            .addOnSuccessListener {
                liveDataResult.mutable.value = Resource.loading(false)
                liveDataFirebaseUser.mutable.value = auth.currentUser
            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
            }
    }
    fun getAllUSer() = viewModelScope.launch {
        firebaseRepo.getUsersFromFirestore()
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }
    fun getPetByIdFromFirestore(petCardModel: PetCardModel) = viewModelScope.launch {
        petCardModel.petPost?.petId?.let { id ->
            firebaseRepo.getPetByIdFromFirestore(id)
                .addOnSuccessListener {

                }.addOnFailureListener {
                    liveDataResult.mutable.value = Resource.loading(false)
                    it.message?.let { message ->
                        liveDataResult.mutable.value = Resource.error(message)
                    }
                }
        }
    }

    fun getUserDataByDocumentId(petCardModel: PetCardModel) = viewModelScope.launch {
        petCardModel.pet?.ownerId?.let { id ->
            firebaseRepo.getUserDataByDocumentId(id)
                .addOnSuccessListener {

                }.addOnFailureListener {
                    liveDataResult.mutable.value = Resource.loading(false)
                    it.message?.let { message ->
                        liveDataResult.mutable.value = Resource.error(message)
                    }
                }
        }

        // liveDataPetCardModels.mutable.value = petCardModel
    }

    fun logout() {
        auth.signOut()
    }
}