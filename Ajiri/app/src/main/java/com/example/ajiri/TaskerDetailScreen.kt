package com.example.ajiri

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
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
    var isLoading by remember { mutableStateOf(true) } // Track loading state
    val db = Firebase.firestore

    // Fetch tasker details from Firestore
    LaunchedEffect(key1 = taskerId) {
        db.collection("taskers").document(taskerId).get()
            .addOnSuccessListener { document ->
                taskerDetail = TaskerDetail(
                    id = taskerId,
                    name = document.getString("name") ?: "",
                    photoUrl = document.getString("photoUrl") ?: "",
                    location = document.getString("location") ?: "",
                    skills = document.get("skills") as List<String>? ?: listOf(),
                    rate = document.getDouble("rate") ?: 0.0,
                    rating = (document.get("rating") as? Number)?.toDouble() ?: 0.0
                )
                isLoading = false // Data loaded successfully
            }
            .addOnFailureListener {
                println("Erreur lors de la récupération des détails du tasker : ${it.message}")
                isLoading = false // Error occurred, stop loading
            }
    }
    // ... (Code de la partie 1) ...

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Détails du Tasker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (isLoading) {
            // Display a loading indicator while data is loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF006400))
            }
        } else {
            taskerDetail?.let { tasker ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Image at the top
                    AsyncImage(
                        model = tasker.photoUrl,
                        contentDescription = "Photo de ${tasker.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = tasker.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // ... (Code des parties 1 et 2) ...

                    // Tasker details section
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TaskerDetailItem(label = "Location", value = tasker.location)
                        TaskerDetailItem(label = "Compétences", value = tasker.skills.joinToString(", "))
                        TaskerDetailItem(label = "Tarif horaire", value = "$${tasker.rate}")
                        TaskerDetailItem(label = "Évaluation", value = "${tasker.rating} étoiles")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Book Appointment button
                    Button(
                        onClick = {
                            val userId = getCurrentUserId()
                            navController.navigate("appointment/${tasker.id}/$userId")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Prendre un rendez-vous",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } ?: run {
                // Handle case where taskerDetail is still null after loading is finished
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tasker introuvable.", color = Color.Red) // Error message
                }
            }
        }
    }
}

// Reusable Composable for Tasker Detail Items
@Composable
fun TaskerDetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray // Light gray for the label
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
fun getCurrentUserId(): String {
    val currentUser = FirebaseAuth.getInstance().currentUser
    return currentUser?.uid ?: "" // Retourner l'ID utilisateur ou une chaîne vide s'il est null
}

