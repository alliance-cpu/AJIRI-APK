package com.example.ajiri

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
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
fun AppointmentScreen(navController: NavController, taskerId: String) {
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
        Text("Schedule an Appointment", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = { showDatePicker(context) { date = it } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.CalendarToday, contentDescription = "Select Date")
            Spacer(Modifier.width(8.dp))
            Text(if (date.isEmpty()) "Select Date" else date)
        }
        Button(
            onClick = { showTimePicker(context) { time = it } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Timer, contentDescription = "Select Time")
            Spacer(Modifier.width(8.dp))
            Text(if (time.isEmpty()) "Select Time" else time)
        }
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Additional Notes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Hide keyboard */ })
        )
        Button(
            onClick = {
                saveAppointment(db, taskerId, date, time, notes)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Book Appointment")
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            onDateSelected(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _: TimePicker, hourOfDay: Int, minute: Int ->
            onTimeSelected(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

fun saveAppointment(db: FirebaseFirestore, taskerId: String, date: String, time: String, notes: String) {
    val appointmentDetails = hashMapOf(
        "taskerId" to taskerId,
        "date" to date,
        "time" to time,
        "notes" to notes
    )
    db.collection("appointments").add(appointmentDetails)
        .addOnSuccessListener {
            println("Appointment saved successfully!")
        }
        .addOnFailureListener { e ->
            println("Error saving appointment: ${e.message}")
        }
}
