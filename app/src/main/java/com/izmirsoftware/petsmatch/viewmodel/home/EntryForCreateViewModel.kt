package com.izmirsoftware.petsmatch.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izmirsoftware.petsmatch.model.Comments
import com.izmirsoftware.petsmatch.model.Location
import com.izmirsoftware.petsmatch.model.Owner
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EntryForCreateViewModel
@Inject
constructor(

) : BaseViewModel() {
    val petCardModel: LiveData<List<Pet>> = MutableLiveData()

    fun createPetCardModels() {

        val pet = Pet()
        pet.profileImage = "https://cdn1.ntv.com.tr/gorsel/xXM8GccvjkmZxggiDVHP5g.jpg"
        pet.name = "Fuat"
        pet.id = UUID.randomUUID().toString()

        petCardModel.mutable.value = listOf(
            pet,
            pet
        )
    }
}
