package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.scentra.uicontroller.view.widget.ScentraBottomAppBar
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHistory(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ScentraTopAppBar(
                title = "Riwayat Stok",
                canNavigateBack = false,
            )
        },
        bottomBar = {
            ScentraBottomAppBar(
                currentRoute = "history",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ini Halaman History")
        }
    }
}