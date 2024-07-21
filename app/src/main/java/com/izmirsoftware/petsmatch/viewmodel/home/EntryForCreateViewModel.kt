package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.toPetModel
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryForCreateViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : BaseViewModel() {
    val liveDataPets: LiveData<List<Pet>> = MutableLiveData()

    fun getPetsByUserId(userId: String) = viewModelScope.launch {
        liveDataResult.mutable.value = Resource.loading(true)
        firebaseRepo.getPetsByUserId(userId)
            .addOnSuccessListener {
                liveDataPets.mutable.value = it.map { snapshot -> snapshot.toPetModel() }
                liveDataResult.mutable.value = Resource.loading(false)
            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
            }
    }
}