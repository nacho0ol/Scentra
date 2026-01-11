package com.example.scentra.uicontroller.route

object DestinasiUpdate : DestinasiNavigasi {
    override val route = "update"
    override val titleRes = "Edit Produk"
    const val idProduk = "idProduk"
    val routeWithArgs = "$route/{$idProduk}"
}