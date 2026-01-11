package com.example.scentra.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class AddProdukResponse(
    val success: Boolean,
    val message: String
)