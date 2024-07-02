package com.example.ajiri

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AppointmentScreen(navController: NavController, taskerId: String, userId: String) {
    val context = LocalContext.current
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Prendre un rendez-vous", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = { showDatePicker(context) { date = it } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Choisir une date")
            Spacer(Modifier.width(8.dp))
            Text(if (date.isEmpty()) "Choisir une date" else date)
        }
        Button(
            onClick = { showTimePicker(context) { time = it } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Timer, contentDescription = "Choisir l'heure")
            Spacer(Modifier.width(8.dp))
            Text(if (time.isEmpty()) "Choisir l'heure" else time)
        }
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("commentaires") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Hide keyboard */ })
        )
        Button(
            onClick = {
                saveAppointment(db, taskerId, date, time, notes, userId)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Prendre le rendez-vous")
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Vérifier si la date sélectionnée est antérieure à la date actuelle
            if (selectedDate.timeInMillis < calendar.timeInMillis) {
                // Afficher un message d'erreur ou réinitialiser la date
                // (vous pouvez choisir l'action à effectuer ici)
                Toast.makeText(context, "Veuillez sélectionner une date future.", Toast.LENGTH_SHORT).show()
            } else {
                onDateSelected(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            }
        },
        currentYear,
        currentMonth,
        currentDay
    ).show()
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _: TimePicker, hourOfDay: Int, minute: Int ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)

            // Vérifier si l'heure sélectionnée est antérieure à l'heure actuelle (uniquement si la date est aujourd'hui)
            if (selectedTime.timeInMillis < calendar.timeInMillis && dateIsToday(calendar)) {
                // Afficher un message d'erreur ou réinitialiser l'heure
                Toast.makeText(context, "Veuillez sélectionner une heure future.", Toast.LENGTH_SHORT).show()
            } else {
                onTimeSelected(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
            }
        },
        currentHour,
        currentMinute,
        true
    ).show()
}

// Fonction utilitaire pour vérifier si la date est aujourd'hui
private fun dateIsToday(calendar: Calendar): Boolean {
    val today = Calendar.getInstance()
    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
}

fun saveAppointment(db: FirebaseFirestore, taskerId: String, date: String, time: String, notes: String, userId: String) {
    val appointmentDetails = hashMapOf(
        "taskerId" to taskerId,
        "date" to date,
        "time" to time,
        "notes" to notes,
        "userId" to userId // Ajout de l'ID utilisateur au document
    )
    db.collection("appointments").add(appointmentDetails)
        .addOnSuccessListener {
            println("Rendez-vous enregistré avec succès !")
        }
        .addOnFailureListener { e ->
            println("Erreur lors de l'enregistrement du rendez-vous : ${e.message}")
        }
}
