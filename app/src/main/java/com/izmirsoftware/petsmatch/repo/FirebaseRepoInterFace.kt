package com.izmirsoftware.petsmatch.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.UserModel

interface FirebaseRepoInterFace {
    // Auth
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>


    // Firestore
    // Firestore - User
    fun addUserToFirestore(data: UserModel): Task<Void>
    fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot>
    fun getUsersFromFirestore(): Task<QuerySnapshot>
    fun updateUserData(userId: String, updateData: HashMap<String, Any?>): Task<Void>

    //Firestore - Pet
    fun addPetToFirestore(petId: String, pet: Pet): Task<Void>
    fun deletePetFromFirestore(petId: String): Task<Void>
    fun getAllPetsFromFirestore(limit: Long): Task<QuerySnapshot>
    fun getPetByIdFromFirestore(petId: String): Task<DocumentSnapshot>
    fun getPetsByUserId(userId: String): Task<QuerySnapshot>
}