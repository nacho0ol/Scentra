package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.scentra.modeldata.Produk
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel
import com.example.scentra.viewmodel.DetailUiState
import com.example.scentra.viewmodel.DetailViewModel

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

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ScentraTopAppBar(
                title = "Detail Produk",
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    // 1. Icon Edit
                    IconButton(onClick = { onEditClick(idProduk) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    // 2. Icon Delete
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val state = viewModel.detailUiState) {
            is DetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is DetailUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Gagal memuat data") }
            is DetailUiState.Success -> {
                DetailContent(
                    produk = state.produk,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Produk?") },
                text = { Text("Yakin mau hapus? Data yang dihapus tidak bisa balik lagi lho.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteProduk(idProduk) {
                            showDeleteDialog = false
                            navigateBack()
                        }
                    }) { Text("Hapus", color = Color.Red) }
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gambar
        AsyncImage(
            model = produk.imgPath,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(300.dp)
        )

        // Info Utama
        Text(produk.nama, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Rp ${produk.price}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)

        // Pake .stok (sesuai model kamu)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SuggestionChip(onClick = {}, label = { Text("Stok: ${produk.stok}") })
            SuggestionChip(onClick = {}, label = { Text("${produk.variant} ml") })
        }

        Divider()

        // Notes
        ItemDetail("Top Notes", produk.topNotes)
        ItemDetail("Middle Notes", produk.middleNotes)
        ItemDetail("Base Notes", produk.baseNotes)

        Divider()

        // Deskripsi
        ItemDetail("Deskripsi", produk.description)

        // Spacer buat napas di bawah (biar gak mentok layar banget)
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun ItemDetail(judul: String, isi: String) {
    Column {
        Text(judul, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(isi, style = MaterialTheme.typography.bodyLarge)
    }
}