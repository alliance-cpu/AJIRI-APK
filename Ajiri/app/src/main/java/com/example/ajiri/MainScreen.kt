package com.example.ajiri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun rememberUserId(): String? {
    // Assume FirebaseAuth has been initialized elsewhere in your application
    val auth = FirebaseAuth.getInstance()
    return remember { auth.currentUser?.uid }
}
@Composable
fun MainScreen(navController: NavController) {
    val userId = rememberUserId()  // Retrieve userId from FirebaseAuth

    if (userId == null) {
        // Handle user not logged in scenario
        Text("Please log in")
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to Ajiri!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            MainButton(
                text = "Browse Categories",
                icon = Icons.Filled.Category,
                onClick = { navController.navigate("categories") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            MainButton(
                text = "My Dashboard",
                icon = Icons.Filled.Dashboard,
                onClick = { navController.navigate("userDashboard/$userId") }
            )
        }
    }
}

@Composable
fun MainButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
