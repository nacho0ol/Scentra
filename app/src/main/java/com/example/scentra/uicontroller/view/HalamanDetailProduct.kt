package com.example.scentra.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scentra.modeldata.CurrentUser
import com.example.scentra.modeldata.Produk
import com.example.scentra.uicontroller.viewmodel.DetailUiState
import com.example.scentra.uicontroller.viewmodel.DetailViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

private val StockInColor = Color(0xFF398256)
private val StockOutColor = Color(0xFF922B21)

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
                title = {
                    Text(
                        "Detail Produk",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
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
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = StockOutColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        when (val state = viewModel.detailUiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Gagal memuat data: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
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
                title = { Text("Hapus Produk", fontWeight = FontWeight.Bold) },
                text = { Text("Apakah Anda yakin ingin menghapus produk ini secara permanen? Data tidak dapat dikembalikan.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteProduk(idProduk); showDeleteDialog = false; navigateBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Hapus") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
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
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = getDetailImageUrl(produk.imgPath),
            contentDescription = produk.nama,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .offset(y = (-24).dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = produk.nama,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Rp ${produk.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text("Stok Gudang", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    }

                    Text(
                        "${produk.stok} pcs",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onStockOutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = StockOutColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Keluar", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onRestockClick,
                        colors = ButtonDefaults.buttonColors(containerColor = StockInColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Masuk", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            Text("Spesifikasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProductDetailItem(Icons.Outlined.LocalDrink, "Varian", "${produk.variant} ml")
                Divider(Modifier.padding(start = 40.dp), color = Color.LightGray.copy(alpha = 0.3f))
                ProductDetailItem(Icons.Outlined.Spa, "Top Notes", produk.topNotes)
                Divider(Modifier.padding(start = 40.dp), color = Color.LightGray.copy(alpha = 0.3f))
                ProductDetailItem(Icons.Outlined.Opacity, "Middle Notes", produk.middleNotes)
                Divider(Modifier.padding(start = 40.dp), color = Color.LightGray.copy(alpha = 0.3f))
                ProductDetailItem(Icons.Outlined.FormatQuote, "Base Notes", produk.baseNotes)
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Tentang Produk", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = produk.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProductDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = Color.Black)
        }
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
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            reasons.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { selectedReason = option; expanded = false })
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val jumlah = textInput.toIntOrNull() ?: 0
                    if (jumlah > 0) onConfirm(jumlah, selectedReason)
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White) else Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

private fun getDetailImageUrl(path: String): String {
    val baseUrl = "http://10.0.2.2:3000/uploads/"
    return if (path.startsWith("http")) path else "$baseUrl$path"
}