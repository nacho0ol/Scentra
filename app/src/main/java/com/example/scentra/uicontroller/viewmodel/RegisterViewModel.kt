package com.example.scentra.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.modeldata.RegisterRequest
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

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

    var isFirstnameError by mutableStateOf(false)
    var isLastnameError by mutableStateOf(false)
    var isUsernameError by mutableStateOf(false)
    var isPasswordError by mutableStateOf(false)

    fun onFirstnameChange(it: String) { firstname = it; isFirstnameError = false }
    fun onLastnameChange(it: String) { lastname = it; isLastnameError = false }
    fun onUsernameChange(it: String) { username = it; isUsernameError = false }
    fun onPasswordChange(it: String) { password = it; isPasswordError = false }

    fun onRegisterClick() {
        var hasError = false
        if (firstname.isBlank()) { isFirstnameError = true; hasError = true }
        if (lastname.isBlank()) { isLastnameError = true; hasError = true }
        if (username.isBlank()) { isUsernameError = true; hasError = true }
        if (password.isBlank()) { isPasswordError = true; hasError = true }

        if (hasError) {
            registerState = RegisterUiState.Error("Mohon lengkapi semua data!")
            return
        }
        viewModelScope.launch {
            registerState = RegisterUiState.Loading
            try {
                val request = RegisterRequest(firstname, lastname, username, password, role)
                val response = repository.register(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    registerState = RegisterUiState.Success(response.body()!!.message)
                } else {
                    val errorBody = response.errorBody()?.string()

                    try {
                        val jsonObject = JSONObject(errorBody)
                        val msg = jsonObject.getString("message")

                        val errorField = if (jsonObject.has("error_field")) jsonObject.getString("error_field") else ""

                        when(errorField) {
                            "firstname" -> isFirstnameError = true
                            "lastname" -> isLastnameError = true
                            "username" -> isUsernameError = true
                        }

                        registerState = RegisterUiState.Error(msg)
                    } catch (e: Exception) {
                        registerState = RegisterUiState.Error("Gagal Register: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                registerState = RegisterUiState.Error("Gagal koneksi: ${e.message}")
            }
        }
    }

    fun resetState() {
        registerState = RegisterUiState.Idle
        firstname = ""; lastname = ""; username = ""; password = ""
        isFirstnameError = false; isLastnameError = false; isUsernameError = false; isPasswordError = false
    }
}