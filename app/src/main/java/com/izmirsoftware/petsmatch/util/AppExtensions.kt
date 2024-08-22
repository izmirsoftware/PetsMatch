package com.izmirsoftware.petsmatch.util

import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.izmirsoftware.petsmatch.model.Pet
import com.izmirsoftware.petsmatch.model.PetPost
import com.izmirsoftware.petsmatch.model.UserModel

//String öğelerin sonuna fonksiyon ile snackbar çıkarma özelliği sağlar
fun String.snackbar(view: View, duration: Int = Toast.LENGTH_SHORT): Snackbar {
    return Snackbar.make(
        view,
        this,
        duration
    )
        .apply { show() }
}

//String öğelerin sonuna fonksiyon ile toast çıkarma özelliği sağlar
fun String.toast(view: View, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(
        view.context,
        this,
        duration
    )
        .apply { show() }
}

//Modellerdeki değişkenler ile veritabanı değişkenleri arasında uyumsuzluk olursa uygulamanın çökmemesi için eklendi

fun QueryDocumentSnapshot.toUserModel(): UserModel? = try {
    toObject(UserModel::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getUserModel",
            it
        )
    }
    UserModel()
}

fun DocumentSnapshot.toUserModel(): UserModel? = try {
    toObject(UserModel::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getUserModel",
            it
        )
    }
    UserModel()
}

fun QueryDocumentSnapshot.toPetModel(): Pet = try {
    toObject(Pet::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getPetModel",
            it
        )
    }
    Pet()
}

fun DocumentSnapshot.toPetModel(): Pet = try {
    toObject(Pet::class.java) ?: Pet()
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getPetModel",
            it
        )
    }
    Pet()
}
fun DocumentSnapshot.toPetPost(): PetPost? = try {
    toObject(PetPost::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getPetPost",
            it
        )
    }
    PetPost()
}
/*
fun QueryDocumentSnapshot.toVilla(): Villa = try {
    toObject(Villa::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getVilla",
            it
        )
    }
    Villa()
}

fun DocumentSnapshot.toVilla(): Villa? = try {
    toObject(Villa::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getVilla",
            it
        )
    }
    Villa()
}

fun DocumentSnapshot.toReservation(): ReservationModel? = try {
    toObject(ReservationModel::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getReservation",
            it
        )
    }
    ReservationModel()
}

fun DocumentSnapshot.toNotification(): InAppNotificationModel? = try {
    toObject(InAppNotificationModel::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getNotification",
            it
        )
    }
    InAppNotificationModel()
}

fun QueryDocumentSnapshot.toReview(): ReviewModel = try {
    toObject(ReviewModel::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getReview",
            it
        )
    }
    ReviewModel()
}

fun DocumentSnapshot.toReview(): ReviewModel? = try {
    toObject(ReviewModel::class.java)
} catch (e: Exception) {
    e.message?.let {
        Log.e(
            "getReview",
            it
        )
    }
    ReviewModel()
}
 */