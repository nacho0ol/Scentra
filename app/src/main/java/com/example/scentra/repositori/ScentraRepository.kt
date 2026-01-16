package com.example.scentra.repositori

import com.example.scentra.apiservice.ScentraApiService
import com.example.scentra.modeldata.AddProdukResponse
import com.example.scentra.modeldata.BaseResponse
import com.example.scentra.modeldata.BasicResponse
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.modeldata.CurrentUser
import com.example.scentra.modeldata.HistoryLog
import com.example.scentra.modeldata.LoginRequest
import com.example.scentra.modeldata.LoginResponse
import com.example.scentra.modeldata.Produk
import com.example.scentra.modeldata.ProdukResponse
import com.example.scentra.modeldata.RegisterRequest
import com.example.scentra.modeldata.RegisterResponse
import com.example.scentra.modeldata.StokRequest
import com.example.scentra.modeldata.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ScentraRepository {

    suspend fun login(username: String, password: String): LoginResponse

    suspend fun register(request: RegisterRequest): Response<RegisterResponse>
    suspend fun getProducts(): ProdukResponse

    suspend fun insertProduk(
        nama: RequestBody,
        variant: RequestBody,
        price: RequestBody,
        stok: RequestBody,
        top: RequestBody,
        middle: RequestBody,
        base: RequestBody,
        desc: RequestBody,
        image: MultipartBody.Part?
    ): Response<BasicResponse>

    suspend fun getProductById(id: Int): Produk

    suspend fun deleteProduct(id: Int)

    suspend fun updateProduct(
        id: Int,
        nama: RequestBody,
        variant: RequestBody,
        price: RequestBody,
        stok: RequestBody,
        top: RequestBody,
        middle: RequestBody,
        base: RequestBody,
        desc: RequestBody,
        oldImgPath: RequestBody,
        image: MultipartBody.Part?
    ): Response<BasicResponse>
    suspend fun restockProduct(productId: Int, qty: Int): Response<Unit>

    suspend fun stockOutProduct(productId: Int, qty: Int, reason: String): Response<Unit>

    suspend fun getHistory(): List<HistoryLog>

    suspend fun getAllUsers(): List<UserData>

    suspend fun getUserById(id: Int): UserData
    suspend fun updateUser(id: Int, user: UserData): Response<BasicResponse>
    suspend fun deleteUser(id: Int)
}

class NetworkScentraRepository(
    private val apiService: ScentraApiService
) : ScentraRepository {
    override suspend fun login(username: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(username, password))
    }

    override suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }

    override suspend fun getProducts(): ProdukResponse {
        return apiService.getProducts()
    }

    override suspend fun insertProduk(
        nama: RequestBody,
        variant: RequestBody,
        price: RequestBody,
        stok: RequestBody,
        top: RequestBody,
        middle: RequestBody,
        base: RequestBody,
        desc: RequestBody,
        image: MultipartBody.Part?
    ): Response<BasicResponse> {
        return apiService.insertProduk(nama, variant, price, stok, top, middle, base, desc, image)
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

    override suspend fun updateProduct(
        id: Int,
        nama: RequestBody,
        variant: RequestBody,
        price: RequestBody,
        stok: RequestBody,
        top: RequestBody,
        middle: RequestBody,
        base: RequestBody,
        desc: RequestBody,
        oldImgPath: RequestBody,
        image: MultipartBody.Part?
    ): Response<BasicResponse> {
        return apiService.updateProduk(id, nama, variant, price, stok, top, middle, base, desc, oldImgPath, image)
    }

    override suspend fun restockProduct(productId: Int, qty: Int): Response<Unit> {
        val userIdYangLogin = CurrentUser.id
        val finalId = if (userIdYangLogin != 0) userIdYangLogin else 1
        val request = StokRequest(productId = productId, userId = finalId, qty = qty)

        return apiService.restockProduct(request)
    }

    override suspend fun stockOutProduct(productId: Int, qty: Int, reason: String): Response<Unit> {
        val userIdYangLogin = CurrentUser.id
        val finalId = if (userIdYangLogin != 0) userIdYangLogin else 1
        val request = StokRequest(productId = productId, userId = finalId, qty = qty, reason = reason)

        return apiService.stockOutProduct(request)
    }

    override suspend fun getHistory(): List<HistoryLog> {
        val response = apiService.getHistoryLog()
        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun getAllUsers(): List<UserData> {
        val response = apiService.getAllUsers()
        if (response.success) {
            return response.data
        } else {
            throw Exception("Gagal ambil data user")
        }
    }

    override suspend fun getUserById(id: Int): UserData {
        val response = apiService.getUserById(id)
        return response.data ?: throw Exception("Data kosong")
    }

    override suspend fun updateUser(id: Int, user: UserData): Response<BasicResponse> {
        return apiService.updateUser(id, user)
    }

    override suspend fun deleteUser(id: Int) {
        val response = apiService.deleteUser(id)
        if (!response.success) throw Exception(response.message)
    }

}