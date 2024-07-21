package com.izmirsoftware.petsmatch.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.izmirsoftware.petsmatch.model.Comment
import com.izmirsoftware.petsmatch.model.Location
import com.izmirsoftware.petsmatch.model.Owner
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetCardModel
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.model.UserModel
import com.izmirsoftware.petsmatch.repo.FirebaseRepoInterFace
import com.izmirsoftware.petsmatch.util.Resource
import com.izmirsoftware.petsmatch.util.toUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val currentUserId = auth.currentUser?.uid.toString()

    private var _reservationCount = MutableLiveData<Int>()
    val reservationCount: LiveData<Int>
        get() = _reservationCount

    private var _resetPasswordMessage = MutableLiveData<Resource<Boolean>>()
    val resetPasswordMessage: LiveData<Resource<Boolean>>
        get() = _resetPasswordMessage

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _userInfoMessage = MutableLiveData<Resource<Boolean>>()
    val userInfoMessage: LiveData<Resource<Boolean>>
        get() = _userInfoMessage


    private var _exitMessage = MutableLiveData<Resource<Boolean>>()
    val exitMessage: LiveData<Resource<Boolean>>
        get() = _exitMessage

    init {
        getUserData()
    }

    private fun getUserData() = viewModelScope.launch {
        _userInfoMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _userInfoMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _userInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun logout() {
        auth.signOut()
    }

    val petCardModel = MutableLiveData<List<PetCardModel>>()

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
            Comment(
                rating = 4.8
            )
        )

        petCardModel.value = listOf(
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