package com.example.scentra.uicontroller.view.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScentraBottomAppBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Home") },
            selected = currentRoute?.contains("dashboard") == true,
            onClick = { onNavigate("dashboard") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == "history",
            onClick = { onNavigate("history") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute?.contains("profile") == true,
            onClick = { onNavigate("profile") }
        )
    }
}