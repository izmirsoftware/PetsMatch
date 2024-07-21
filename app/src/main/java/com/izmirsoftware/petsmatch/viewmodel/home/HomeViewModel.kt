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
import com.izmirsoftware.petsmatch.util.Resource
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
    val liveDataPetCardModels: LiveData<List<PetCardModel>> = MutableLiveData()
    val liveDataFirebaseUser: LiveData<FirebaseUser> = MutableLiveData()

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
    init {
        getAllUSer()
    }

    fun getAllUSer() = viewModelScope.launch {
        firebaseRepo.getUsersFromFirestore()
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    fun getAllPostsFromFirestore(petCardModel: PetCardModel) = viewModelScope.launch {
        firebaseRepo.getAllPostsFromFirestore(10)
            .addOnSuccessListener {

            }.addOnFailureListener {
                liveDataResult.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataResult.mutable.value = Resource.error(message)
                }
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

    fun createPetCardModels() {
        val petPost = PetPost()
        petPost.title = "Çok sevimli kedi"
        petPost.description = "Kedimiz çok sakindir, saldırganlık yapmaz, tuvalet eğitimi var."
        petPost.location = Location(
            city = "İzmir",
            district = "Torbalı"
        )
        val currentTime = System.currentTimeMillis()
        petPost.date = Date(currentTime)

        val pet = Pet()
        pet.profileImage = "https://cdn1.ntv.com.tr/gorsel/xXM8GccvjkmZxggiDVHP5g.jpg"

        val owner = Owner(
            comments = listOf(
                Comment(
                    rating = 4.8
                ),
                Comment(
                    rating = 4.0
                ),
                Comment(
                    rating = 3.5
                )
            )
        )

        liveDataPetCardModels.mutable.value = listOf(
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            ),
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            ),
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            ),
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            ),
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            ),
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            ),
            PetCardModel(
                petPost = petPost,
                pet = pet,
                owner = owner
            )
        )
    }
}