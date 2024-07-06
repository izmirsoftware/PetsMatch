package com.izmirsoftware.petsmatch.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.UserModel
import javax.inject.Inject


class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    //private val notificationAPI: NotificationAPI
) : FirebaseRepoInterFace {

    //Firestore
    private val userCollection = firestore.collection("users")
    private val notificationCollection = firestore.collection("notifications")
    private val petCollection = firestore.collection("pets")
    private val reviewCollection = firestore.collection("reviews")

    //StorageRef
    private val imagesParentRef = storage.reference.child("users")

    //RealtimeRef
    //private val messagesReference = database.getReference("messages")

    //Auth
    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(
            email,
            password
        )
    }

    override fun forgotPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    override fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(
            email,
            password
        )
    }


    // Firestore - User
    override fun addUserToFirestore(data: UserModel): Task<Void> {
        return userCollection.document(data.userId.toString())
            .set(data)
    }

    override fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot> {
        return userCollection.document(documentId)
            .get()
    }

    override fun getUsersFromFirestore(): Task<QuerySnapshot> {
        return userCollection.get()
    }

    override fun updateUserData(userId: String, updateData: HashMap<String, Any?>): Task<Void> {
        return userCollection.document(userId)
            .update(updateData)
    }

    // Firestore - Vila
    override fun addPetToFirestore(petId: String, pet: Pet): Task<Void> {
        return petCollection.document(petId).set(pet)
    }

    override fun deletePetFromFirestore(petId: String): Task<Void> {
        return petCollection.document(petId).delete()
    }

    override fun getAllPetsFromFirestore(limit: Long): Task<QuerySnapshot> {
        return petCollection.limit(limit).get()
    }

    override fun getPetByIdFromFirestore(petId: String): Task<DocumentSnapshot> {
        return petCollection.document(petId).get()
    }

    override fun getPetsByUserId(userId: String): Task<QuerySnapshot> {
        return petCollection.whereEqualTo("ownerId", userId)
            .get()
    }
}