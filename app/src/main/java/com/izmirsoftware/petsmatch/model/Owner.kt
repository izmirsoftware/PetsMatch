package com.izmirsoftware.petsmatch.model

data class Owner(
    var id: String? = null, // Hayvan sahibinin benzersiz kimliği
    var firstName: String? = null, // Hayvan sahibinin adı
    var lastName: String? = null, // Hayvan sahibinin soyadı
    var phone: String? = null, // Hayvan sahibinin telefonu
    var email: String? = null, // Hayvan sahibinin e-posta adresi
    var profileImage: String? = null, // Hayvan sahibinin profil resmi
    var comments: List<Comments>? = null // Hayvan sahibi için yapılan yorumlar ve Puanlar
)

data class Comments(
    var comment: String? = null, // Hayvan sahibi için yapılan yorum
    var rating: Double? = null, // Hayvan sahibi için verilen puan
)