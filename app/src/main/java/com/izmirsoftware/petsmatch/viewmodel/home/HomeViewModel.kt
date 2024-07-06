package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.izmirsoftware.petsmatch.model.Comments
import com.izmirsoftware.petsmatch.model.Location
import com.izmirsoftware.petsmatch.model.Owner
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import java.util.*
import javax.inject.Inject

class HomeViewModel
@Inject
constructor(

): BaseViewModel() {
    val petCardModel: LiveData<List<PetCardModel>> = MutableLiveData()

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

        val owner = Owner()
        owner.comments = listOf(
            Comments(
                rating = 4.8
            )
        )

        petCardModel.mutable.value = listOf(
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