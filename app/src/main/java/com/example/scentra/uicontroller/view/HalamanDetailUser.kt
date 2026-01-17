package com.example.scentra.uicontroller.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scentra.R
import com.example.scentra.modeldata.UserData
import com.example.scentra.uicontroller.viewmodel.UserDetailUiState
import com.example.scentra.uicontroller.viewmodel.UserDetailViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

private val RedButton = Color(0xFF922B21)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailUser(
    idUser: Int,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(idUser) {
        viewModel.getUserById(idUser)
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail User", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                    }

                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedButton)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { innerPadding ->

        when (val state = viewModel.uiState) {
            is UserDetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is UserDetailUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UserDetailUiState.Success -> {
                val user = state.user

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(Color(0xFFFAFAFA)) // Background sedikit abu biar Card nonjol
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // FOTO PROFIL
                    Image(
                        painter = painterResource(id = R.drawable.group_47897),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // NAMA & USERNAME
                    Text(
                        text = "${user.firstname} ${user.lastname ?: ""}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "@${user.username}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // INFO CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            InfoRow(label = "Role", value = user.role)
                            Divider(Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                            InfoRow(label = "User ID", value = "#${user.id}")
                            Divider(Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                            PasswordRow(password = user.password ?: "Tidak ada password")
                        }
                    }
                }

                // DIALOG EDIT
                if (showEditDialog) {
                    EditUserDialog(
                        user = user,
                        onDismiss = { showEditDialog = false },
                        onConfirm = { fname, lname, role ->
                            viewModel.updateUser(user.id, fname, lname, role)
                            showEditDialog = false
                        }
                    )
                }

                // DIALOG DELETE
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Hapus User?") },
                        text = { Text("Yakin ingin menghapus ${user.firstname}? Tindakan ini tidak bisa dibatalkan.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.deleteUser(user.id)
                                    showDeleteDialog = false
                                    navigateBack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RedButton)
                            ) { Text("Hapus") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
                        },
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun PasswordRow(password: String) {
    var isVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Password", fontWeight = FontWeight.SemiBold, color = Color.Gray)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isVisible) password else "•".repeat(8),
                fontWeight = FontWeight.Bold,
                color = if (isVisible) Color.Black else Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(
                onClick = { isVisible = !isVisible },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = "Toggle Password",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EditUserDialog(
    user: UserData,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var fname by remember { mutableStateOf(user.firstname) }
    var lname by remember { mutableStateOf(user.lastname ?: "") }
    var role by remember { mutableStateOf(user.role) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fname,
                    onValueChange = { fname = it },
                    label = { Text("Nama Depan") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = lname,
                    onValueChange = { lname = it },
                    label = { Text("Nama Belakang") },
                    singleLine = true
                )

                Text("Role User:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = role == "Admin", onClick = { role = "Admin" })
                    Text("Admin")
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = role == "Staff", onClick = { role = "Staff" })
                    Text("Staff")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(fname, lname, role) }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        containerColor = Color.White
    )
}