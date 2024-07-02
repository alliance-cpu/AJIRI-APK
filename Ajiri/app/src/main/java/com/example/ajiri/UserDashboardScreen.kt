package com.example.ajiri

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Appointment(
    val id: String = "",
    val taskerName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(navController: NavHostController, userId: String) {
    val appointments = remember { mutableStateListOf<Appointment>() }
    val db = Firebase.firestore

    LaunchedEffect(userId) { // Relance l'effet si userId change
        val listener = db.collection("appointments")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("UserDashboardScreen", "Listen failed.", e)
                    return@addSnapshotListener
                }

                // Mettre à jour la liste des rendez-vous
                appointments.clear()
                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        appointments.add(document.toAppointment())
                    }
                }
            }

        // Optionnel : supprimer l'écouteur lorsque l'écran est détruit
    }
    // ... (Code de la partie 1) ...

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Tableau de bord",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        contentColor = Color.Black,
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (appointments.isEmpty()) {
                item {
                    Text(
                        text = "Aucun rendez-vous programmé.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(appointments) { appointment ->
                    AppointmentCard(appointment, navController)
                }
            }
        }
    }
}
@Composable
fun AppointmentCard(appointment: Appointment, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("appointmentDetails/${appointment.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = "Icône de rendez-vous",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF006400)
            )
            Column {
                Text(
                    text = "Tasker : ${appointment.taskerName}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text("Date : ${appointment.date}", color = Color.Black)
                Text("Heure : ${appointment.time}", color = Color.Black)
                Text("Statut : ${appointment.status}",
                    color = when (appointment.status) {
                        "Confirmé" -> Color.Green
                        "Annulé" -> Color.Red
                        else -> Color.Gray
                    }
                )
            }
        }
    }
}

// Fonction pour convertir un document Firestore en un objet Appointment
fun com.google.firebase.firestore.DocumentSnapshot.toAppointment(): Appointment {
    return Appointment(
        id = this.id,
        taskerName = this.getString("taskerName") ?: "",
        date = this.getString("date") ?: "",
        time = this.getString("time") ?: "",
        status = this.getString("status") ?: ""
    )
}