package com.example.scentra.uicontroller.route

object DestinasiDetail : DestinasiNavigasi {
    override val route = "detail"
    override val titleRes = "Detail Produk"
    const val idProduk = "idProduk"
    val routeWithArgs = "$route/{$idProduk}"
}