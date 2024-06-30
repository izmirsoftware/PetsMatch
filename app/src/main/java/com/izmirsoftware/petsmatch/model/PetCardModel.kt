package com.izmirsoftware.petsmatch.model

/**Bu model veritabanından alınan bilgileri
 * uygulama içinde tek model üzerinde birleştirmek için kullanılmaktadır
 */
data class PetCardModel(
    var petPost: PetPost? = null, // Veritabanından gelen ilan
    var pet: Pet? = null, // Veritabanından gelen hayvan
    var owner: Owner? = null, // Veritabanından gelen sahip
)
