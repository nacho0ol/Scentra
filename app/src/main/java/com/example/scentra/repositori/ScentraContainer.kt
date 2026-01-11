package com.example.scentra.repositori

import com.example.scentra.apiservice.ScentraApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val scentraRepository: ScentraRepository
}

class ScentraContainer : AppContainer {
    private val baseUrl = "http://10.0.2.2:3000/"

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: ScentraApiService by lazy {
        retrofit.create(ScentraApiService::class.java)
    }

    override val scentraRepository: ScentraRepository by lazy {
        NetworkScentraRepository(retrofitService)
    }
}