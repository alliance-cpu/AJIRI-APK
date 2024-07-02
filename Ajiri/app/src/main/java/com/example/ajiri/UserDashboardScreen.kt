package com.example.ajiri

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Appointment(
    val id: String,
    val taskerName: String,
    val date: String,
    val time: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(navController: NavController, userId: String) {
    val appointments = remember { mutableStateListOf<Appointment>() }
    val db = Firebase.firestore


    LaunchedEffect(key1 = userId) {
        db.collection("appointments")
            .whereEqualTo("userId", userId)
            .orderBy("date")
            .get()
            .addOnSuccessListener { snapshot ->
                appointments.clear()
                appointments.addAll(snapshot.documents.map { document ->
                    Appointment(
                        id = document.id,
                        taskerName = document.getString("taskerName") ?: "Unknown Tasker",
                        date = document.getString("date") ?: "No Date",
                        time = document.getString("time") ?: "No Time",
                        status = document.getString("status") ?: "Pending"
                    )
                })
            }
            .addOnFailureListener {
                println("Error fetching appointments: ${it.message}")
            }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Your Dashboard") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(8.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCard(appointment, navController)
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("appointmentDetails/${appointment.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Appointment Icon", modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Tasker: ${appointment.taskerName}", style = MaterialTheme.typography.titleMedium)
                Text("Date: ${appointment.date}", style = MaterialTheme.typography.bodyLarge)
                Text("Time: ${appointment.time}", style = MaterialTheme.typography.bodyLarge)
                Text("Status: ${appointment.status}", color = when (appointment.status) {
                    "Confirmed" -> Color.Green
                    "Cancelled" -> Color.Red
                    else -> Color.Gray
                })
            }
        }
    }
}