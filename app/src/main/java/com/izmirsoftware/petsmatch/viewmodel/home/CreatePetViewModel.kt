package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
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

    fun getPetImagesSample() {
        val pets: List<String> = listOf(
            "https://images.squarespace-cdn.com/content/v1/54822a56e4b0b30bd821480c/45ed8ecf-0bb2-4e34-8fcf-624db47c43c8/Golden+Retrievers+dans+pet+care.jpeg",
            "https://cdn.mos.cms.futurecdn.net/ASHH5bDmsp6wnK6mEfZdcU-1200-80.jpg",
            "https://www.princeton.edu/sites/default/files/styles/1x_full_2x_half_crop/public/images/2022/02/KOA_Nassau_2697x1517.jpg?itok=Bg2K7j7J",
            "https://hips.hearstapps.com/hmg-prod/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=0.752xw:1.00xh;0.175xw,0&resize=1200:*",
            "https://www.campbellrivervet.com/wp-content/uploads/sites/282/2022/05/Husky-1000x650.jpg",
            "https://d.newsweek.com/en/full/2239959/cul-map-dogs-01-banner.jpg?w=1600&h=1600&l=51&t=46&q=88&f=9bc0ce5022e0e7e92bcf8c7eafde6f47",
        )

        liveDataImages.mutable.value = pets.toList()
    }

    fun addImageAndPetToFirebase(
        images: MutableList<ByteArray>,
        petModel: Pet,
        uploadedImages: MutableList<String> = mutableListOf()
    ) {
        petModel.id?.let { petId ->
            if (images.size > 0) {
                val image = images[0]
                liveDataStatus.mutable.value = Resource.loading(true)
                firestoreRepo.addPetImage(petId, userId, image)
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl.addOnSuccessListener { uri ->
                            images.removeAt(0)
                            uploadedImages.add(uri.toString())
                            liveDataImages.mutable.value = uploadedImages.toList()
                            addImageAndPetToFirebase(images, petModel, uploadedImages)
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
            } else {
                petModel.imagesUrl = uploadedImages
                updatePetToFirestore(petId, petModel)
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