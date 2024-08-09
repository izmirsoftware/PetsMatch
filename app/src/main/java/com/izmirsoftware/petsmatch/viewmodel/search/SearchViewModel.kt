package com.izmirsoftware.petsmatch.viewmodel.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.PetType
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.toPetPost
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _searchResult = MutableLiveData<List<PetPost>>()
    val searchResult: LiveData<List<PetPost>>
        get() = _searchResult


    init {
        _firebaseMessage.value = Resource.loading(null)
        getPetsFromFirestore()
    }

    private fun getPetsFromFirestore() = viewModelScope.launch {
        firebaseRepo.getAllPostsFromFirestore()
            .addOnSuccessListener {
                val postList = mutableListOf<PetPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toPetPost()?.let { post ->
                        postList.add(post)
                        println("pet : "+post.id)
                    }
                }
                _searchResult.value = postList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
            }
    }

    fun logout(){
        auth.signOut()
    }
}