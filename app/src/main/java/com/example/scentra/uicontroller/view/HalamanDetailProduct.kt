package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scentra.modeldata.CurrentUser
import com.example.scentra.modeldata.Produk
import com.example.scentra.uicontroller.viewmodel.DetailUiState
import com.example.scentra.uicontroller.viewmodel.DetailViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailProduct(
    idProduk: Int,
    navigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(idProduk) {
        viewModel.getProdukById(idProduk)
    }

    var showRestockDialog by remember { mutableStateOf(false) }
    var showStockOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val stockOutReasons = listOf("Sales", "Tester", "Damaged", "Lost")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (CurrentUser.role == "Admin") {
                        IconButton(onClick = { onEditClick(idProduk) }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = viewModel.detailUiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = "Error: ${state.message}") }
            }
            is DetailUiState.Success -> {
                LaunchedEffect(viewModel.isStockLoading) {
                    if (!viewModel.isStockLoading && viewModel.stockErrorMessage == null) {
                        showRestockDialog = false
                        showStockOutDialog = false
                    }
                }

                DetailContent(
                    produk = state.produk,
                    onRestockClick = { viewModel.resetStockError(); showRestockDialog = true },
                    onStockOutClick = { viewModel.resetStockError(); showStockOutDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showRestockDialog) {
            StockDialog(
                title = "Restock Barang (+)",
                label = "Jumlah Masuk",
                errorMessage = viewModel.stockErrorMessage,
                isLoading = viewModel.isStockLoading,
                reasons = emptyList(),
                onDismiss = { showRestockDialog = false; viewModel.resetStockError() },
                onValueChange = { viewModel.resetStockError() },
                onConfirm = { jumlah, _ ->
                    viewModel.updateStok(idProduk, jumlah, isRestock = true)
                }
            )
        }

        if (showStockOutDialog) {
            StockDialog(
                title = "Barang Keluar (-)",
                label = "Jumlah Keluar",
                errorMessage = viewModel.stockErrorMessage,
                isLoading = viewModel.isStockLoading,
                reasons = stockOutReasons,
                onDismiss = { showStockOutDialog = false; viewModel.resetStockError() },
                onValueChange = { viewModel.resetStockError() },
                onConfirm = { jumlah, selectedReason ->
                    viewModel.updateStok(idProduk, jumlah, isRestock = false, reason = selectedReason)
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Produk") },
                text = { Text("Yakin hapus permanen?") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteProduk(idProduk); showDeleteDialog = false; navigateBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Hapus") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun DetailContent(
    produk: Produk,
    onRestockClick: () -> Unit,
    onStockOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth().height(250.dp), shape = MaterialTheme.shapes.medium) {
            AsyncImage(
                model = getDetailImageUrl(produk.imgPath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column {
            Text(text = produk.nama, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = "Rp ${produk.price}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
        Divider()
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Stok Gudang", style = MaterialTheme.typography.titleMedium)
                    Text("${produk.stok}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onStockOutClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)), modifier = Modifier.weight(1f)) { Text("Keluar (-)") }
                    Button(onClick = onRestockClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)), modifier = Modifier.weight(1f)) { Text("Masuk (+)") }
                }
            }
        }
        Divider()
        DetailInfoRow(label = "Variant", value = "${produk.variant} ml")
        DetailInfoRow(label = "Top Notes", value = produk.topNotes)
        DetailInfoRow(label = "Middle Notes", value = produk.middleNotes)
        DetailInfoRow(label = "Base Notes", value = produk.baseNotes)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Deskripsi:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(produk.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
        Text(text = value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDialog(
    title: String,
    label: String,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    reasons: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onValueChange: () -> Unit = {},
    onConfirm: (Int, String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf(if(reasons.isNotEmpty()) reasons[0] else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            textInput = it
                            onValueChange()
                        }
                    },
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (reasons.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedReason,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Alasan Keluar") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            reasons.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedReason = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val jumlah = textInput.toIntOrNull() ?: 0
                    if (jumlah > 0) {
                        onConfirm(jumlah, selectedReason)
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

fun getDetailImageUrl(path: String): String {
    val baseUrl = "http://10.0.2.2:3000/uploads/"
    return if (path.startsWith("http")) path else "$baseUrl$path"
}