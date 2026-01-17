package com.example.scentra.uicontroller.view.widget

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ScentraBottomAppBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color(0xFFFFFCF5),
        tonalElevation = 0.dp
    ) {
        // --- ITEM 1: HOME ---
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Home") },
            selected = currentRoute?.contains("dashboard") == true,
            onClick = { onNavigate("dashboard") },
            colors = customNavColors()
        )

        // --- ITEM 2: HISTORY ---
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == "history",
            onClick = { onNavigate("history") },
            colors = customNavColors()
        )

        // --- ITEM 3: PROFILE ---
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute?.contains("profile") == true,
            onClick = { onNavigate("profile") },
            colors = customNavColors()
        )
    }
}

@Composable
fun customNavColors() = NavigationBarItemDefaults.colors(
    indicatorColor = Color(0xFF1D1B20),
    selectedIconColor = Color.White,
    selectedTextColor = Color.Black,
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray
)