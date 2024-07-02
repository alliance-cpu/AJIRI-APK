package com.example.ajiri

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class TaskerDetail(
    val id: String,
    val name: String,
    val photoUrl: String,
    val location: String,
    val skills: List<String>,
    val rate: Double,
    val rating: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskerDetailScreen(navController: NavController, taskerId: String) {
    var taskerDetail by remember { mutableStateOf<TaskerDetail?>(null) }
    val db = Firebase.firestore

    // Fetch tasker details from Firestore
    LaunchedEffect(key1 = taskerId) {
        db.collection("taskers").document(taskerId).get().addOnSuccessListener { document ->
            taskerDetail = TaskerDetail(
                id = taskerId,
                name = document.getString("name") ?: "",
                photoUrl = document.getString("photoUrl") ?: "",
                location = document.getString("location") ?: "",
                skills = document.get("skills") as List<String>? ?: listOf(),
                rate = document.getDouble("rate") ?: 0.0,
                rating = (document.get("rating") as? Number)?.toDouble() ?: 0.0
            )
        }.addOnFailureListener {
            println("Error fetching tasker details: ${it.message}")
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Tasker Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) {
        taskerDetail?.let { tasker ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text(tasker.name, style = MaterialTheme.typography.headlineMedium, color = Color.Black)
                Text("Location: ${tasker.location}", color = Color.Black)
                Text("Skills: ${tasker.skills.joinToString(", ")}", color = Color.Black)
                Text("Hourly Rate: $${tasker.rate}", color = Color.Black)
                Text("Rating: ${tasker.rating} stars", color = Color.Black)
                // Add call to action buttons if necessary
                Button(
                    onClick = { /* Navigate to appointment booking */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Appointment")
                }
            }
        } ?: run {
            // Show loading or not found message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading tasker details...")
            }
        }
    }
}
