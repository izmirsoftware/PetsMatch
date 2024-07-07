package com.izmirsoftware.petsmatch.model

data class Pet(
    var id: String? = null, // Hayvanın benzersiz kimliği
    var genus: Genus? = null, // Hayvanın türü (kedi veya köpek)
    var name: String? = null, // Hayvanın ismi
    var gender: Gender? = null, // Hayvanın cinsiyeti
    var age: Int? = null, // Hayvanın yaşı
    var breed: String? = null, // Hayvanın ırkı
    var color: String? = null, // Hayvanın rengi
    var profileImage: String? = null, // Hayvanın profil resmi URL'si
    var imagesUrl: List<String>? = null, // Hayvanın diğer resim URL'leri
    var duringEstrus: Boolean? = null, // Hayvan kızgınlık döneminde mi?
    var personality: String? = null, // Hayvanın kişilik tanımı
    var interests: List<String>? = null, // Hayvanın ilgi alanları
    var vaccinations: Boolean? = null, // Hayvanın aşıları tam mı?
    var allergies: List<String>? = null, // Hayvanın alerjileri
    var ownerId: String? = null // Hayvanın sahibinin kimliği
)

enum class Genus {
    CAT,
    DOG
}

enum class Gender {
    MALE,
    FEMALE
}