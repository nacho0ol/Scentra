package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.uicontroller.viewmodel.RegisterUiState
import com.example.scentra.uicontroller.viewmodel.RegisterViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRegister(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.registerState
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            onNavigateBack()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Daftar Akun Baru") })
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState), // Aktifkan scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = viewModel.firstname,
                onValueChange = { viewModel.firstname = it },
                label = { Text("Nama Depan") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.lastname,
                onValueChange = { viewModel.lastname = it },
                label = { Text("Nama Belakang") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PILIHAN ROLE (Radio Button) ---
            Text("Pilih Role:", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = viewModel.role == "Staff",
                    onClick = { viewModel.role = "Staff" }
                )
                Text("Staff")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = viewModel.role == "Admin",
                    onClick = { viewModel.role = "Admin" }
                )
                Text("Admin")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onRegisterClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading
            ) {
                if (uiState is RegisterUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp)
                } else {
                    Text("Daftar Sekarang")
                }
            }

            TextButton(onClick = onNavigateBack) {
                Text("Batal / Sudah punya akun")
            }

            if (uiState is RegisterUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}