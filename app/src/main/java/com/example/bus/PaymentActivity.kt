package com.example.bus

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var btnConfirmBooking: Button
    private lateinit var tvPaymentInfo: TextView

    private lateinit var selectedSeats: List<Int>
    private var totalPrice = 0
    private lateinit var busId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        checkNotificationPermission()

        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod)
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking)
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo)

        busId = intent.getStringExtra("busId") ?: ""
        selectedSeats = intent.getStringExtra("selectedSeats")?.split(",")?.map { it.toInt() } ?: listOf()
        totalPrice = intent.getIntExtra("totalPrice", 0)

        val paymentOptions = listOf("bKash", "Nagad", "Cash")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = adapter

        tvPaymentInfo.text = "Total Payment: ৳$totalPrice\nSelected Seats: ${selectedSeats.joinToString()}"

        btnConfirmBooking.setOnClickListener {
            val paymentMethod = spinnerPaymentMethod.selectedItem.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            val bookingId = FirebaseDatabase.getInstance().reference.push().key ?: return@setOnClickListener
            val bookingData = mapOf(
                "userId" to userId,
                "busId" to busId,
                "seats" to selectedSeats,
                "totalPrice" to totalPrice,
                "paymentMethod" to paymentMethod,
                "timestamp" to System.currentTimeMillis()
            )

            FirebaseDatabase.getInstance().getReference("bookings")
                .child(bookingId)
                .setValue(bookingData)
                .addOnSuccessListener {
                    val bookedSeatsRef = FirebaseDatabase.getInstance().getReference("buses/$busId/bookedSeats")
                    selectedSeats.forEach { seatNo ->
                        bookedSeatsRef.child(seatNo.toString()).setValue(userId)
                    }

                    showInstantNotification()
                    scheduleNotification(busId)
                    Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Booking failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showInstantNotification() {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("title", "Bus Ticket Booked")
        intent.putExtra("message", "Thank you for booking seat(s): ${selectedSeats.joinToString()}")

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Show notification 1 second from now
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent)
    }

    private fun scheduleNotification(busId: String) {
        val busRef = FirebaseDatabase.getInstance().getReference("buses/$busId")
        busRef.get().addOnSuccessListener { snapshot ->
            val date = snapshot.child("date").value.toString()      // e.g., "30-05-2025"
            val time = snapshot.child("departureTime").value.toString()  // e.g., "14:30"

            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val departureTime = formatter.parse("$date $time") ?: return@addOnSuccessListener

            val calendar = Calendar.getInstance()
            calendar.time = departureTime
            calendar.add(Calendar.MINUTE, -30)  // Notify 30 mins before departure

            val notifyTime = calendar.timeInMillis

            val intent = Intent(this, NotificationReceiver::class.java)
            intent.putExtra("title", "Bus Reminder")
            intent.putExtra("message", "Your bus departs in 30 minutes! Seats: ${selectedSeats.joinToString()}")

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                busId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
