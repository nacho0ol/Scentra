package com.example.scentra.apiservice

import com.example.scentra.modeldata.AddProdukResponse
import com.example.scentra.modeldata.BaseResponse
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.modeldata.LoginRequest
import com.example.scentra.modeldata.LoginResponse
import com.example.scentra.modeldata.ProdukDetailResponse
import com.example.scentra.modeldata.ProdukResponse
import com.example.scentra.modeldata.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ScentraApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): BaseResponse

    @GET("api/products")
    suspend fun getProducts(): ProdukResponse

    @POST("api/products")
    suspend fun insertProduk(@Body produk: CreateProdukRequest): AddProdukResponse

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProdukDetailResponse

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): retrofit2.Response<Unit>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body produk: CreateProdukRequest
    ): retrofit2.Response<Unit>
}