package com.example.scentra.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)


@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData? = null
)

@Serializable
data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val username: String,
    val password: String,
    val role: String // "Admin" atau "Staff"
)

@Serializable
data class BaseResponse(
    val success: Boolean,
    val message: String
)

@kotlinx.serialization.Serializable
data class UserData(
    val user_id: Int,
    val firstname: String,
    val role: String
)