package com.example.scentra.uicontroller.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ExitToApp
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
import com.example.scentra.uicontroller.view.widget.ScentraBottomAppBar
import com.example.scentra.uicontroller.viewmodel.ProfileUiState
import com.example.scentra.uicontroller.viewmodel.ProfileViewModel
import com.example.scentra.uicontroller.viewmodel.provider.PenyediaViewModel

private val RedButton = Color(0xFF922B21)

@Composable
fun HalamanProfile(
    onLogoutClick: () -> Unit,
    onAddStaffClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onUserClick: (Int) -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        if (viewModel.currentUser.role == "Admin") {
            viewModel.loadUsers()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    val BackgroundColor = Color(0xFFFFFBF2)

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1D1B20))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        bottomBar = {
            ScentraBottomAppBar(
                currentRoute = "profile",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))


            Image(
                painter = painterResource(id = R.drawable.group_47897),
                contentDescription = "Profile Pic",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.Black, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hello ${viewModel.currentUser.role}!",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = viewModel.currentUser.username,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.currentUser.role == "Admin") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage User",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )

                    IconButton(
                        onClick = onAddStaffClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1D1B20))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add User",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (val state = viewModel.profileUiState) {
                    is ProfileUiState.Loading -> CircularProgressIndicator()
                    is ProfileUiState.Error -> Text("Gagal memuat: ${state.message}", color = Color.Red)
                    is ProfileUiState.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(state.users) { user ->
                                UserCard(
                                    user = user,
                                    onClick = {
                                        onUserClick(user.id) }
                                )

                            }

                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.CenterEnd) {
                                    Text("See more", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = {
                    showLogoutDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedButton),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Icon(Icons.Outlined.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Konfirmasi Logout") },
                text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.logout()
                            onLogoutClick()
                            showLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedButton)
                    ) {
                        Text("Ya, Keluar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun UserCard(user: UserData,
             onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.firstname,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "@${user.username}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = user.role,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (user.role == "Admin") Color.Blue else Color.Gray
            )
        }
    }
}