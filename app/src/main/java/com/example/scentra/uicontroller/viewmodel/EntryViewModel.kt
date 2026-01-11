package com.example.scentra.uicontroller.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.CreateProdukRequest
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch

data class InsertUiState(
    val nama: String = "",
    val variant: String = "",
    val topNotes: String = "",
    val middleNotes: String = "",
    val baseNotes: String = "",
    val description: String = "",
    val currentStock: String = "",
    val price: String = "",
    val error: String? = null,
    val imageUri: Uri? = null
)

class EntryViewModel(private val repository: ScentraRepository) : ViewModel() {
    var uiState by mutableStateOf(InsertUiState())
        private set

    fun updateUiState(newState: InsertUiState) {
        uiState = newState
    }

    fun saveProduk(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = CreateProdukRequest(
                    nama = uiState.nama,
                    variant = uiState.variant.toIntOrNull() ?: 0,
                    topNotes = uiState.topNotes,
                    middleNotes = uiState.middleNotes,
                    baseNotes = uiState.baseNotes,
                    description = uiState.description,
                    price = uiState.price.toIntOrNull() ?: 0,
                    currentStock = uiState.currentStock.toIntOrNull() ?: 0,
                    imgPath = "default.jpg"
                )

                repository.insertProduk(request)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(error = "Gagal simpan: ${e.message}")
            }
        }
    }
}