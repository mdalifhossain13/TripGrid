package com.example.bus

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddBusActivity : AppCompatActivity() {

    private lateinit var etBusName: EditText
    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etPrice: EditText
    private lateinit var btnAddBus: Button

    private val totalSeats = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bus)

        etBusName = findViewById(R.id.etBusName)
        etFrom = findViewById(R.id.etFrom)
        etTo = findViewById(R.id.etTo)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etPrice = findViewById(R.id.etPrice)
        btnAddBus = findViewById(R.id.btnAddBus)

        // Show Date Picker on click
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etDate.setText(formattedDate)
            }, year, month, day)

            datePicker.show()
        }

        // Show Time Picker on click
        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                etTime.setText(formattedTime)
            }, hour, minute, true)

            timePicker.show()
        }

        btnAddBus.setOnClickListener {
            val busName = etBusName.text.toString().trim()
            val from = etFrom.text.toString().trim()
            val to = etTo.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val price = etPrice.text.toString().trim()

            if (busName.isEmpty() || from.isEmpty() || to.isEmpty() || date.isEmpty() || time.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val busId = FirebaseDatabase.getInstance().reference.push().key!!
            val busData = mapOf(
                "busName" to busName,
                "from" to from,
                "to" to to,
                "date" to date,
                "departureTime" to time,
                "price" to price.toInt(),
                "totalSeats" to totalSeats,
                "bookedSeats" to emptyMap<String, String>()
            )

            FirebaseDatabase.getInstance().getReference("buses")
                .child(busId)
                .setValue(busData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Bus added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add bus", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
