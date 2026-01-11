package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.uicontroller.view.widget.ProdukCard
import com.example.scentra.uicontroller.view.widget.ScentraBottomAppBar
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel
import com.example.scentra.viewmodel.DashboardUiState
import com.example.scentra.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDashboard(
    role: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onAddProductClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            ScentraTopAppBar(
                title = "Scentra",
                canNavigateBack = false,
            )
        },
        bottomBar = {
            ScentraBottomAppBar(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "profile" -> onNavigateToProfile()
                        "history" -> onNavigateToHistory()
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == "Admin") {
                FloatingActionButton(onClick = onAddProductClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
                }
            }
        }
    ) { innerPadding ->

        Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {

            when (uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DashboardUiState.Success -> {
                    if (uiState.produk.isEmpty()) {
                        Text(
                            text = "Belum ada produk. Klik + untuk tambah.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.produk) { produk ->
                                ProdukCard(produk = produk, onClick = { onProductClick(produk.id) })
                            }
                        }
                    }
                }

                is DashboardUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Gagal memuat data :(")
                        Button(onClick = { viewModel.getProducts() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}