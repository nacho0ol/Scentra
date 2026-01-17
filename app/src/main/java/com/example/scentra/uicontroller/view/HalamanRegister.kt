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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.uicontroller.view.widget.ScentraTopAppBar
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
            ScentraTopAppBar(
                title = "Daftar Akun Baru",
                canNavigateBack = true,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState), // Scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (uiState is RegisterUiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }


            OutlinedTextField(
                value = viewModel.firstname,
                onValueChange = { viewModel.onFirstnameChange(it) },
                label = { Text("Nama Depan *") },
                isError = viewModel.isFirstnameError,
                supportingText = {
                    if(viewModel.isFirstnameError) Text("Wajib diisi (Min 3 Huruf)", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.lastname,
                onValueChange = { viewModel.onLastnameChange(it) },
                label = { Text("Nama Belakang *") },
                isError = viewModel.isLastnameError,
                supportingText = {
                    if(viewModel.isLastnameError) Text("Wajib diisi (Min 3 Huruf)", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text("Username *") },
                isError = viewModel.isUsernameError,
                supportingText = {
                    if(viewModel.isUsernameError) Text("Min 6 karakter, depannya huruf", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password *") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                isError = viewModel.isPasswordError,
                supportingText = {
                    if(viewModel.isPasswordError) Text("Wajib diisi", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PILIHAN ROLE ---
            Text(
                text = "Pilih Role:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.Start)) {
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

            // --- TOMBOL DAFTAR ---
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")
            }

        }
    }
}