package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.RegisterRequest
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch

sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    data class Success(val message: String) : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

class RegisterViewModel(private val repository: ScentraRepository) : ViewModel() {
    var registerState: RegisterUiState by mutableStateOf(RegisterUiState.Idle)
        private set

    var firstname by mutableStateOf("")
    var lastname by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    var role by mutableStateOf("Staff")

    fun onRegisterClick() {
        viewModelScope.launch {
            registerState = RegisterUiState.Loading
            try {
                val request = RegisterRequest(firstname, lastname, username, password, role)

                val response = repository.register(request)

                if (response.success) {
                    registerState = RegisterUiState.Success(response.message)
                } else {
                    registerState = RegisterUiState.Error(response.message)
                }
            } catch (e: Exception) {
                registerState = RegisterUiState.Error("Gagal daftar: ${e.message}")
            }
        }
    }

    fun resetState() {
        registerState = RegisterUiState.Idle
    }
}