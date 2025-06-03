package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var seatLayout: LinearLayout
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnConfirm: Button

    private val selectedSeats = mutableSetOf<Int>()
    private val bookedSeats = mutableSetOf<Int>()  // ✅ Already booked
    private var seatPrice = 0
    private val totalSeats = 30
    private var busId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)

        seatLayout = findViewById(R.id.seatLayout)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        btnConfirm = findViewById(R.id.btnConfirm)

        seatPrice = intent.getIntExtra("price", 0)
        busId = intent.getStringExtra("busId") ?: ""

        fetchBookedSeatsAndRenderUI()

        btnConfirm.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("selectedSeats", selectedSeats.joinToString(","))
                putExtra("totalPrice", selectedSeats.size * seatPrice)
                putExtra("busId", busId)
                putExtra("seatPrice", seatPrice)
                putExtra("departureTime", intent.getStringExtra("departureTime"))
                putExtra("busName", intent.getStringExtra("busName"))
                putExtra("date", intent.getStringExtra("date"))
                putExtra("from", intent.getStringExtra("from"))
                putExtra("to", intent.getStringExtra("to"))
            }
            startActivity(intent)
        }
    }

    private fun fetchBookedSeatsAndRenderUI() {
        val dbRef = FirebaseDatabase.getInstance().getReference("buses/$busId/bookedSeats")
        dbRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach {
                bookedSeats.add(it.key.toString().toInt())
            }
            renderSeatUI()
        }.addOnFailureListener {
            renderSeatUI()  // Even if fails, show UI
        }
    }

    private fun renderSeatUI() {
        var seatNumber = 1
        for (row in 1..6) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 8)
            }

            for (i in 1..3) {
                rowLayout.addView(createSeatButton(seatNumber++))
            }

            rowLayout.addView(Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(32, LinearLayout.LayoutParams.MATCH_PARENT)
            })

            for (i in 1..2) {
                rowLayout.addView(createSeatButton(seatNumber++))
            }

            if (row == 1) {
                val driverImage = ImageView(this).apply {
                    setImageResource(R.drawable.d)
                    layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                        marginStart = 16
                    }
                }
                rowLayout.addView(driverImage)
            }

            seatLayout.addView(rowLayout)
        }
    }

    private fun createSeatButton(seatNo: Int): ToggleButton {
        return ToggleButton(this).apply {
            text = seatNo.toString()
            textOn = seatNo.toString()
            textOff = seatNo.toString()
            setTextColor(ContextCompat.getColor(this@SeatSelectionActivity, android.R.color.white))
            setPadding(12, 8, 12, 8)
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                marginEnd = 12
            }

            if (bookedSeats.contains(seatNo)) {
                isEnabled = false
                setBackgroundResource(R.drawable.seat_booked_bg) // ✅ booked seat background
            } else {
                setBackgroundResource(R.drawable.seat_available_bg)
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedSeats.add(seatNo)
                        setBackgroundResource(R.drawable.seat_selected_bg)
                    } else {
                        selectedSeats.remove(seatNo)
                        setBackgroundResource(R.drawable.seat_available_bg)
                    }
                    updateTotal()
                }
            }
        }
    }

    private fun updateTotal() {
        tvTotalPrice.text = "Total: ৳${selectedSeats.size * seatPrice}"
    }
}
