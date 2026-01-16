package com.example.scentra.uicontroller.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scentra.modeldata.getFullImageUrl
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel
import com.example.scentra.viewmodel.EditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditProduct(
    idProduk: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.updateUiState(uiState.copy(imageUri = uri))
    }

    LaunchedEffect(idProduk) {
        viewModel.loadProduk(idProduk)
    }

    Scaffold(
        topBar = {
            ScentraTopAppBar(
                title = "Edit Produk",
                canNavigateBack = true,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. ERROR GLOBAL (Server Error)
            if (uiState.error != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // 2. FOTO PRODUK
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.imageUri != null) {
                        AsyncImage(
                            model = uiState.imageUri,
                            contentDescription = "New Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = getFullImageUrl(uiState.imgPath),
                            contentDescription = "Old Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ganti Foto", modifier = Modifier.size(20.dp))
                    }
                }
            }


            item {
                OutlinedTextField(
                    value = uiState.nama,
                    onValueChange = { viewModel.updateUiState(uiState.copy(nama = it, isNamaError = false)) },
                    label = { Text("Nama Produk") },
                    isError = uiState.isNamaError,
                    supportingText = { if (uiState.isNamaError) Text("Wajib diisi (Min 5 Char)", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.variant,
                    onValueChange = { viewModel.updateUiState(uiState.copy(variant = it, isVariantError = false)) },
                    label = { Text("Varian (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.isVariantError,
                    supportingText = { if (uiState.isVariantError) Text("Wajib angka valid", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = { viewModel.updateUiState(uiState.copy(price = it, isPriceError = false)) },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.isPriceError,
                    supportingText = { if (uiState.isPriceError) Text("Wajib angka (> 30.000)", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.currentStock,
                    onValueChange = { viewModel.updateUiState(uiState.copy(currentStock = it, isStockError = false)) },
                    label = { Text("Stok") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.isStockError,
                    supportingText = { if (uiState.isStockError) Text("Wajib angka (0-99)", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                Column {
                    DynamicNotesInput(
                        label = "Top Notes",
                        currentValue = uiState.topNotes,
                        onValueChange = { viewModel.updateUiState(uiState.copy(topNotes = it, isTopNotesError = false)) }
                    )
                    if (uiState.isTopNotesError) {
                        Text(
                            text = "Notes tidak valid",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }

            item {
                Column {
                    DynamicNotesInput(
                        label = "Middle Notes",
                        currentValue = uiState.middleNotes,
                        onValueChange = { viewModel.updateUiState(uiState.copy(middleNotes = it, isMidNotesError = false)) }
                    )
                    if (uiState.isMidNotesError) {
                        Text(
                            text = "Notes tidak valid",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }

            item {
                Column {
                    DynamicNotesInput(
                        label = "Base Notes",
                        currentValue = uiState.baseNotes,
                        onValueChange = { viewModel.updateUiState(uiState.copy(baseNotes = it, isBaseNotesError = false)) }
                    )
                    if (uiState.isBaseNotesError) {
                        Text(
                            text = "Notes tidak valid",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateUiState(uiState.copy(description = it, isDescError = false)) },
                    label = { Text("Deskripsi") },
                    isError = uiState.isDescError,
                    supportingText = { if (uiState.isDescError) Text("Wajib diisi (Min 10 Char)", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateProduk(idProduk, context) { onNavigateBack() }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    enabled = !uiState.isLoading
                ) {
                    if(uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Update Produk")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}