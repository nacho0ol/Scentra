package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.UserData
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: UserData) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val repository: ScentraRepository) : ViewModel() {
    var loginState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    fun onLoginClick() {
        viewModelScope.launch {
            loginState = LoginUiState.Loading
            try {
                val response = repository.login(username, password)
                if (response.success && response.data != null) {
                    loginState = LoginUiState.Success(response.data)
                } else {
                    loginState = LoginUiState.Error(response.message)
                }
            } catch (e: IOException) {
                loginState = LoginUiState.Error("Gagal koneksi server")
            } catch (e: Exception) {
                loginState = LoginUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun resetState() {
        loginState = LoginUiState.Idle
        username = ""
        password = ""
    }
}