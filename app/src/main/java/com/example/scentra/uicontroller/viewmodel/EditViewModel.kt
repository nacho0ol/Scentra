package com.example.scentra.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scentra.repositori.ScentraRepository
import com.example.scentra.uicontroller.viewmodel.InsertUiState
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class EditViewModel(private val repository: ScentraRepository) : ViewModel() {

    var uiState by mutableStateOf(InsertUiState())
        private set

    fun updateUiState(newState: InsertUiState) { uiState = newState }
    fun onNamaChange(it: String) { uiState = uiState.copy(nama = it, isNamaError = false) }

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
                    imgPath = produk.imgPath
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProduk(idProduk: Int, context: Context, onSuccess: () -> Unit) {
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
                val oldPathRB = uiState.imgPath.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                uiState.imageUri?.let { uri ->
                    val file = uriToFile(uri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                val response = repository.updateProduct(
                    idProduk, namaRB, variantRB, priceRB, stokRB, topRB, midRB, baseRB, descRB, oldPathRB, imagePart
                )

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
                        uiState = uiState.copy(isLoading = false, error = "Gagal Update: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "Gagal update: ${e.message}")
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        return tempFile
    }
}