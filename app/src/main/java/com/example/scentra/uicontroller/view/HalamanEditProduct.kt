package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- ERROR MESSAGE ---
            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }


            item {
                OutlinedTextField(
                    value = uiState.nama,
                    onValueChange = { viewModel.updateUiState(uiState.copy(nama = it)) },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.variant,
                    onValueChange = { viewModel.updateUiState(uiState.copy(variant = it)) },
                    label = { Text("Varian (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = { viewModel.updateUiState(uiState.copy(price = it)) },
                    label = { Text("Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.currentStock,
                    onValueChange = { viewModel.updateUiState(uiState.copy(currentStock = it)) },
                    label = { Text("Stok") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                DynamicNotesInput(
                    label = "Top Notes",
                    currentValue = uiState.topNotes,
                    onValueChange = { viewModel.updateUiState(uiState.copy(topNotes = it)) }
                )
            }

            item {
                DynamicNotesInput(
                    label = "Middle Notes",
                    currentValue = uiState.middleNotes,
                    onValueChange = { viewModel.updateUiState(uiState.copy(middleNotes = it)) }
                )
            }

            item {
                DynamicNotesInput(
                    label = "Base Notes",
                    currentValue = uiState.baseNotes,
                    onValueChange = { viewModel.updateUiState(uiState.copy(baseNotes = it)) }
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateUiState(uiState.copy(description = it)) },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateProduk(idProduk, onSuccess = onNavigateBack)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Update Produk")
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}