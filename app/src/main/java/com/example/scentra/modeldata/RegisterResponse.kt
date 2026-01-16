package com.example.scentra.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val success: Boolean,

    val message: String
)