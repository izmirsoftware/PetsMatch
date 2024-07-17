package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EntryForCreateViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : BaseViewModel() {
    val liveDataPets: LiveData<List<Pet>> = MutableLiveData()
}