package com.example.scentra.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produk(
    @SerialName("product_id") val id: Int,
    @SerialName("product_name") val nama: String,
    val variant: Int,
    @SerialName("top_notes") val topNotes: String,
    @SerialName("middle_notes") val middleNotes: String,
    @SerialName("base_notes") val baseNotes: String,
    val description: String,
    val price: Int, // Di DB INT
    @SerialName("img_path") val imgPath: String,
    @SerialName("current_stock") val stok: Int
)

@Serializable
data class ProdukResponse(
    val success: Boolean,
    val message: String,
    val data: List<Produk>
)

@Serializable
data class CreateProdukRequest(
    @SerialName("product_name") val nama: String,
    val variant: Int,
    @SerialName("top_notes") val topNotes: String,
    @SerialName("middle_notes") val middleNotes: String,
    @SerialName("base_notes") val baseNotes: String,
    val description: String,
    val price: Int,
    @SerialName("current_stock") val currentStock: Int,
    @SerialName("img_path") val imgPath: String = "default.jpg"
)