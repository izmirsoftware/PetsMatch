package com.izmirsoftware.petsmatch.model

data class UserModel(
    var userId: String? = null,
    var username: String? = null,
    var email: String? = null,
    var profilePhoto: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var bio: String? = null,
    var token: String? = null
)