package com.example.scentra.repositori

import com.example.scentra.apiservice.ScentraApiService
import com.example.scentra.modeldata.AddProdukResponse
import com.example.scentra.modeldata.BaseResponse
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.modeldata.LoginRequest
import com.example.scentra.modeldata.LoginResponse
import com.example.scentra.modeldata.Produk
import com.example.scentra.modeldata.ProdukResponse
import com.example.scentra.modeldata.RegisterRequest

interface ScentraRepository {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun register(request: RegisterRequest): BaseResponse

    suspend fun getProducts(): ProdukResponse

    suspend fun insertProduk(produk: CreateProdukRequest): AddProdukResponse

    suspend fun getProductById(id: Int): Produk

    suspend fun deleteProduct(id: Int)

    suspend fun updateProduct(id: Int, produk: CreateProdukRequest)

}

class NetworkScentraRepository(
    private val apiService: ScentraApiService
) : ScentraRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(username, password))
    }

    override suspend fun register(request: RegisterRequest): BaseResponse {
        return apiService.register(request)
    }

    override suspend fun getProducts(): ProdukResponse {
        return apiService.getProducts()
    }
    override suspend fun insertProduk(produk: CreateProdukRequest): AddProdukResponse {
        return apiService.insertProduk(produk)
    }

    override suspend fun getProductById(id: Int): Produk {
        return apiService.getProductById(id).data
    }


    override suspend fun deleteProduct(id: Int) {
        try {
            val response = apiService.deleteProduct(id)
            if (!response.isSuccessful) {
                throw Exception("Gagal delete: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateProduct(id: Int, produk: CreateProdukRequest) {
        try {
            val response = apiService.updateProduct(id, produk)
            if (!response.isSuccessful) {
                throw Exception("Gagal update produk: ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}