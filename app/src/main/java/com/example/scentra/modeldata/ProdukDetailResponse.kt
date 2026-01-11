package com.example.scentra.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class ProdukDetailResponse(
    val success: Boolean,
    val data: Produk
)