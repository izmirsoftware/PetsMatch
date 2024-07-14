package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePetViewModel
@Inject
constructor(
    private val firestoreRepo: FirebaseRepoInterFace,
    firebaseAuth: FirebaseAuth
) : BaseViewModel() {
    private val userId = firebaseAuth.currentUser?.uid.toString()

    val liveDataPet: LiveData<Pet> = MutableLiveData()
    val liveDataImages: LiveData<List<String>> = MutableLiveData()

    fun setPetModel(petModel: Pet) {
        liveDataPet.mutable.value = petModel
    }


    fun setImages(images: List<String>) {
        liveDataImages.mutable.value = images.toList()
    }

    fun addImageAndPetToFirebase(
        profileImage: ByteArray?,
        images: MutableList<ByteArray>,
        petModel: Pet,
        uploadedProfileImage: String?,
        uploadedImages: MutableList<String> = mutableListOf()
    ) {
        petModel.id?.let { petId ->
            profileImage?.let { image ->
                liveDataStatus.mutable.value = Resource.loading(true)
                firestoreRepo.addPetImage(petId, userId, image)
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl
                            .addOnSuccessListener { uri ->
                                addImageAndPetToFirebase(
                                    null,
                                    images,
                                    petModel,
                                    uri.toString(),
                                    uploadedImages
                                )
                            }.addOnFailureListener {
                                liveDataStatus.mutable.value = it.localizedMessage?.let { message ->
                                    liveDataStatus.mutable.value = Resource.loading(false)
                                    Resource.error("Fotoğraf url alınamadı.\nHata: $message", null)
                                }
                            }
                    }.addOnFailureListener {
                        liveDataStatus.mutable.value = it.localizedMessage?.let { message ->
                            liveDataStatus.mutable.value = Resource.loading(false)
                            Resource.error("Fotoğraf yüklenemedi.\nHata: $message", null)
                        }
                    }
            } ?: run {
                if (images.size > 0) {
                    val image = images[0]
                    liveDataStatus.mutable.value = Resource.loading(true)
                    firestoreRepo.addPetImage(petId, userId, image)
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl
                                .addOnSuccessListener { uri ->
                                    images.removeAt(0)
                                    uploadedImages.add(uri.toString())
                                    addImageAndPetToFirebase(
                                        null,
                                        images,
                                        petModel,
                                        uploadedProfileImage,
                                        uploadedImages
                                    )
                                }.addOnFailureListener {
                                    liveDataStatus.mutable.value =
                                        it.localizedMessage?.let { message ->
                                            liveDataStatus.mutable.value = Resource.loading(false)
                                            Resource.error(
                                                "Fotoğraf url alınamadı.\nHata: $message",
                                                null
                                            )
                                        }
                                }
                        }.addOnFailureListener {
                            liveDataStatus.mutable.value = it.localizedMessage?.let { message ->
                                liveDataStatus.mutable.value = Resource.loading(false)
                                Resource.error("Fotoğraf yüklenemedi.\nHata: $message", null)
                            }
                        }
                } else {
                    petModel.profileImage = uploadedProfileImage
                    petModel.imagesUrl = uploadedImages
                    updatePetToFirestore(petId, petModel)
                }
            }

        }
    }

    private fun updatePetToFirestore(petId: String, petModel: Pet) {
        liveDataStatus.mutable.value = Resource.loading(true)
        firestoreRepo.addPetToFirestore(petId, petModel)
            .addOnCompleteListener { task ->
                liveDataStatus.mutable.value = Resource.loading(false)
                if (task.isSuccessful) {
                    liveDataStatus.mutable.value = Resource.success(true)
                } else {
                    liveDataStatus.mutable.value =
                        task.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
    }

}