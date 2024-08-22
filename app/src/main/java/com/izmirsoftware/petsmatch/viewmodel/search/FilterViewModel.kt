package com.izmirsoftware.petsmatch.viewmodel.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : BaseViewModel() {
    private val _filteredPets = MutableLiveData<List<Pet>>()
    val filteredPets: LiveData<List<Pet>> get() = _filteredPets

    fun filterPets(genus: String?, gender: String?, age: String?, breed: String?, color: String?, limit: Long) {
        firebaseRepo.getFilteredPosts(genus, gender, age, breed, color, limit)
            .addOnSuccessListener { querySnapshot ->
                val pets = querySnapshot.toObjects(Pet::class.java)
                _filteredPets.value = pets
            }
            .addOnFailureListener { exception ->
                // Hata işlemlerini yapın
                Log.e("PetViewModel", "Error getting filtered posts", exception)
            }
    }
}