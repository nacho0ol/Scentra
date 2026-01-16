package com.example.scentra.apiservice

import com.example.scentra.modeldata.AddProdukResponse
import com.example.scentra.modeldata.BaseResponse
import com.example.scentra.modeldata.BasicResponse
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.modeldata.HistoryResponse
import com.example.scentra.modeldata.LoginRequest
import com.example.scentra.modeldata.LoginResponse
import com.example.scentra.modeldata.ProdukDetailResponse
import com.example.scentra.modeldata.ProdukResponse
import com.example.scentra.modeldata.RegisterRequest
import com.example.scentra.modeldata.RegisterResponse
import com.example.scentra.modeldata.StokRequest
import com.example.scentra.modeldata.UserData
import com.example.scentra.modeldata.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ScentraApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/products")
    suspend fun getProducts(): ProdukResponse

    @Multipart
    @POST("api/products")
    suspend fun insertProduk(
        @Part("product_name") nama: RequestBody,
        @Part("variant") variant: RequestBody,
        @Part("price") price: RequestBody,
        @Part("current_stock") stok: RequestBody,
        @Part("top_notes") top: RequestBody,
        @Part("middle_notes") middle: RequestBody,
        @Part("base_notes") base: RequestBody,
        @Part("description") desc: RequestBody,

        @Part image: MultipartBody.Part?
    ): Response<BasicResponse>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProdukDetailResponse

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): retrofit2.Response<Unit>

    @Multipart
    @PUT("api/products/{id}")
    suspend fun updateProduk(
        @Path("id") id: Int,
        @Part("product_name") nama: RequestBody,
        @Part("variant") variant: RequestBody,
        @Part("price") price: RequestBody,
        @Part("current_stock") stok: RequestBody,
        @Part("top_notes") top: RequestBody,
        @Part("middle_notes") middle: RequestBody,
        @Part("base_notes") base: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("img_path") oldImgPath: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<BasicResponse>

    @POST("api/products/restock")
    suspend fun restockProduct(@Body request: StokRequest): retrofit2.Response<Unit>

    @POST("api/products/stock-out")
    suspend fun stockOutProduct(@Body request: StokRequest): retrofit2.Response<Unit>

    @GET("api/products/history")
    suspend fun getHistoryLog(): HistoryResponse

    @GET("api/auth/users")
    suspend fun getAllUsers(): UserResponse

    @GET("api/auth/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): LoginResponse

    // Update
    @PUT("api/auth/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body user: UserData
    ): Response<BasicResponse>

    // Delete
    @DELETE("api/auth/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): BaseResponse
}