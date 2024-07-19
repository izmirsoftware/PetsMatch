package com.izmirsoftware.petsmatch.model

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

data class Owner(
    var id: String? = null, // Hayvan sahibinin benzersiz kimliği
    var firstName: String? = null, // Hayvan sahibinin adı
    var lastName: String? = null, // Hayvan sahibinin soyadı
    var phone: String? = null, // Hayvan sahibinin telefonu
    var email: String? = null, // Hayvan sahibinin e-posta adresi
    var profileImage: String? = null, // Hayvan sahibinin profil resmi
    var comments: List<Comment>? = null // Hayvan sahibi için yapılan yorumlar ve Puanlar
) {
    var averageRating = "0.0"

    init {
        var sumRating = 0.0
        comments?.let {
            it.forEach { comment -> sumRating += comment.rating ?: 0.0 }

            NumberFormat.getInstance(Locale.getDefault()).apply {
                minimumIntegerDigits = 0
                maximumIntegerDigits = 2
                roundingMode = RoundingMode.UP

                averageRating = format(sumRating / it.size)
            }
        }
    }
}

data class Comment(
    var comment: String? = null, // Hayvan sahibi için yapılan yorum
    var rating: Double? = null, // Hayvan sahibi için verilen puan
)