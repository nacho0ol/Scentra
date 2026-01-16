package com.example.scentra.uicontroller.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.repositori.ScentraRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject // WAJIB ADA
import java.io.File
import java.io.FileOutputStream

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
    val imageUri: Uri? = null,
    val imgPath: String = "default.jpg",
    val isLoading: Boolean = false,
    val isNamaError: Boolean = false,
    val isVariantError: Boolean = false,
    val isPriceError: Boolean = false,
    val isStockError: Boolean = false,
    val isTopNotesError: Boolean = false,
    val isMidNotesError: Boolean = false,
    val isBaseNotesError: Boolean = false,
    val isDescError: Boolean = false
)

class EntryViewModel(private val repository: ScentraRepository) : ViewModel() {
    var uiState by mutableStateOf(InsertUiState())
        private set

    fun updateUiState(newState: InsertUiState) { uiState = newState }

    // Reset Error Functions
    fun onNamaChange(it: String) { uiState = uiState.copy(nama = it, isNamaError = false) }
    fun onVariantChange(it: String) { uiState = uiState.copy(variant = it, isVariantError = false) }
    fun onPriceChange(it: String) { uiState = uiState.copy(price = it, isPriceError = false) }
    fun onStockChange(it: String) { uiState = uiState.copy(currentStock = it, isStockError = false) }
    fun onTopNotesChange(it: String) { uiState = uiState.copy(topNotes = it, isTopNotesError = false) }
    fun onMidNotesChange(it: String) { uiState = uiState.copy(middleNotes = it, isMidNotesError = false) }
    fun onBaseNotesChange(it: String) { uiState = uiState.copy(baseNotes = it, isBaseNotesError = false) }
    fun onDescChange(it: String) { uiState = uiState.copy(description = it, isDescError = false) }

    fun saveProduk(context: Context, onSuccess: () -> Unit) {
        var hasError = false
        var currentState = uiState

        if (currentState.nama.isBlank()) { currentState = currentState.copy(isNamaError = true); hasError = true }
        if (currentState.variant.isBlank()) { currentState = currentState.copy(isVariantError = true); hasError = true }
        if (currentState.price.isBlank()) { currentState = currentState.copy(isPriceError = true); hasError = true }
        if (currentState.currentStock.isBlank()) { currentState = currentState.copy(isStockError = true); hasError = true }
        if (currentState.topNotes.isBlank()) { currentState = currentState.copy(isTopNotesError = true); hasError = true }
        if (currentState.middleNotes.isBlank()) { currentState = currentState.copy(isMidNotesError = true); hasError = true }
        if (currentState.baseNotes.isBlank()) { currentState = currentState.copy(isBaseNotesError = true); hasError = true }
        if (currentState.description.isBlank()) { currentState = currentState.copy(isDescError = true); hasError = true }

        uiState = currentState
        if (hasError) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val namaRB = uiState.nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val variantRB = uiState.variant.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceRB = uiState.price.toRequestBody("text/plain".toMediaTypeOrNull())
                val stokRB = uiState.currentStock.toRequestBody("text/plain".toMediaTypeOrNull())
                val topRB = uiState.topNotes.toRequestBody("text/plain".toMediaTypeOrNull())
                val midRB = uiState.middleNotes.toRequestBody("text/plain".toMediaTypeOrNull())
                val baseRB = uiState.baseNotes.toRequestBody("text/plain".toMediaTypeOrNull())
                val descRB = uiState.description.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                uiState.imageUri?.let { uri ->
                    val file = uriToFile(uri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                val response = repository.insertProduk(namaRB, variantRB, priceRB, stokRB, topRB, midRB, baseRB, descRB, imagePart)

                if (response.isSuccessful && response.body()?.success == true) {
                    uiState = uiState.copy(isLoading = false)
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    try {
                        val jsonObject = JSONObject(errorBody)
                        val msg = jsonObject.getString("message")
                        val errorField = if (jsonObject.has("error_field")) jsonObject.getString("error_field") else ""

                        var newState = uiState.copy(isLoading = false, error = msg)

                        when(errorField) {
                            "product_name" -> newState = newState.copy(isNamaError = true)
                            "variant" -> newState = newState.copy(isVariantError = true)
                            "price" -> newState = newState.copy(isPriceError = true)
                            "current_stock" -> newState = newState.copy(isStockError = true)
                            "top_notes" -> newState = newState.copy(isTopNotesError = true)
                            "middle_notes" -> newState = newState.copy(isMidNotesError = true)
                            "base_notes" -> newState = newState.copy(isBaseNotesError = true)
                            "description" -> newState = newState.copy(isDescError = true)
                        }
                        uiState = newState
                    } catch (e: Exception) {
                        uiState = uiState.copy(isLoading = false, error = "Gagal: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "Error: ${e.message}")
            }
        }
    }
    private fun uriToFile(uri: android.net.Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        return tempFile
    }
}