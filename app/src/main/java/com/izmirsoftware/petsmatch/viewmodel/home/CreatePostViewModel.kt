package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.toPetModel
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel
@Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : BaseViewModel() {
    val liveDataPet: LiveData<Pet> = MutableLiveData()

    fun getPetByIdFromFirestore(petId: String) = viewModelScope.launch {
        liveDataResult.mutable.value = Resource.loading(true)
        firebaseRepo.getPetByIdFromFirestore(petId)
            .addOnSuccessListener {
                liveDataPet.mutable.value = it.toPetModel()
                liveDataResult.mutable.value = Resource.loading(false)
            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
            }
    }

    fun addPostToFirestore(post: PetPost) = viewModelScope.launch {
        post.id?.let { postId ->
            firebaseRepo.addPostToFirestore(postId, post)
                .addOnSuccessListener {
                    liveDataResult.mutable.value = Resource.loading(false)
                    liveDataResult.mutable.value = Resource.success(true)
                }.addOnFailureListener {
                    liveDataResult.mutable.value = Resource.loading(false)
                    it.message?.let { message ->
                        liveDataResult.mutable.value = Resource.error(message)
                    }
                }
        }
    }
}