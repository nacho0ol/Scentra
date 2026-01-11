package com.example.scentra.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.Produk
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface DetailUiState {
    data class Success(val produk: Produk) : DetailUiState
    object Error : DetailUiState
    object Loading : DetailUiState
}

class DetailViewModel(private val repository: ScentraRepository) : ViewModel() {
    var detailUiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    fun getProdukById(id: Int) {
        viewModelScope.launch {
            detailUiState = DetailUiState.Loading
            detailUiState = try {
                val produk = repository.getProductById(id)
                DetailUiState.Success(produk)
            } catch (e: IOException) {
                DetailUiState.Error
            } catch (e: HttpException) {
                DetailUiState.Error
            }
        }
    }

    fun deleteProduk(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(id)
                onSuccess()
            } catch (e: Exception) {
            }
        }
    }
}