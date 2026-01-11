package com.example.scentra.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.repositori.ScentraRepository
import com.example.scentra.uicontroller.viewmodel.InsertUiState
import kotlinx.coroutines.launch

class EditViewModel(private val repository: ScentraRepository) : ViewModel() {

    var uiState by mutableStateOf(InsertUiState())
        private set

    fun loadProduk(id: Int) {
        viewModelScope.launch {
            try {
                val produk = repository.getProductById(id)
                uiState = InsertUiState(
                    nama = produk.nama,
                    variant = produk.variant.toString(),
                    price = produk.price.toString(),
                    currentStock = produk.stok.toString(),
                    topNotes = produk.topNotes,
                    middleNotes = produk.middleNotes,
                    baseNotes = produk.baseNotes,
                    description = produk.description,

                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProduk(idProduk: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = CreateProdukRequest(
                    nama = uiState.nama,
                    variant = uiState.variant.toIntOrNull() ?: 0,
                    currentStock = uiState.currentStock.toIntOrNull() ?: 0,
                    price = uiState.price.toIntOrNull() ?: 0,
                    topNotes = uiState.topNotes,
                    middleNotes = uiState.middleNotes,
                    baseNotes = uiState.baseNotes,
                    description = uiState.description,
                    imgPath = "default.jpg"
                )
                repository.updateProduct(idProduk, request)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Gagal update: ${e.message}")
            }
        }
    }

    fun updateUiState(newUiState: InsertUiState) {
        uiState = newUiState
    }
}