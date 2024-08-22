package com.izmirsoftware.petsmatch.model

import com.izmirsoftware.petsmatch.util.PetType
import java.util.Date

data class PetPost(
    var id: String? = null, // İlanın benzersiz kimliği
    var title: String? = null, // İlanın başlığı
    var description: String? = null, // İlanın açıklaması
    var age: Int? = null, // Hayvan yaşı
    var image: String? = null, // Hayvan resmi
    var date: Date? = null, // İlanın tarihi
    var location: Location? = null, // Hayvanın konumu
    var petId: String? = null, // Hayvanın kimliği
    var type: PetType? = null, // Hayvanın kimliği
)

data class Location(
    var city: String? = null,
    var district: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
)