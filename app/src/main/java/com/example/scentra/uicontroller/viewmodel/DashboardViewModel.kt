package com.example.scentra.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.Produk
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val produk: List<Produk>) : DashboardUiState
    object Error : DashboardUiState
}

class DashboardViewModel(private val repository: ScentraRepository) : ViewModel() {
    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            uiState = try {
                val response = repository.getProducts()
                DashboardUiState.Success(response.data)
            } catch (e: IOException) {
                DashboardUiState.Error
            } catch (e: Exception) {
                DashboardUiState.Error
            }
        }
    }
}