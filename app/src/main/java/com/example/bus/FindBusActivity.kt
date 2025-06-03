package com.example.bus

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*

class FindBusActivity : AppCompatActivity() {

    private lateinit var etFrom: EditText
    private lateinit var etTo: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSearch: Button
    private lateinit var recyclerBusList: RecyclerView

    private lateinit var busAdapter: BusAdapter
    private val busList = mutableListOf<BusModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_bus)

        etFrom = findViewById(R.id.etFrom)
        etTo = findViewById(R.id.etTo)
        etDate = findViewById(R.id.etDate)
        btnSearch = findViewById(R.id.btnSearch)
        recyclerBusList = findViewById(R.id.recyclerBusList)

        recyclerBusList.layoutManager = LinearLayoutManager(this)
        busAdapter = BusAdapter(busList) { selectedBus ->
            val intent = Intent(this, SeatSelectionActivity::class.java).apply {
                putExtra("busId", selectedBus.busId)
                putExtra("busName", selectedBus.busName)
                putExtra("from", selectedBus.from)
                putExtra("to", selectedBus.to)
                putExtra("date", selectedBus.date)
                putExtra("departureTime", selectedBus.departureTime)
                putExtra("price", selectedBus.price)
            }
            startActivity(intent)
        }
        recyclerBusList.adapter = busAdapter

        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(this, { _, y, m, d ->
                val date = String.format("%04d-%02d-%02d", y, m + 1, d)
                etDate.setText(date)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        btnSearch.setOnClickListener {
            searchBuses()
        }
    }

    private fun searchBuses() {
        val fromText = etFrom.text.toString().trim().lowercase()
        val toText = etTo.text.toString().trim().lowercase()
        val dateText = etDate.text.toString().trim()

        if (fromText.isEmpty() || toText.isEmpty() || dateText.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("buses")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                busList.clear()

                for (busSnap in snapshot.children) {
                    try {
                        val bus = busSnap.getValue(BusModel::class.java)
                        if (bus != null) {
                            Log.d("BusData", "From: ${bus.from}, To: ${bus.to}, Date: ${bus.date}")
                            if (
                                bus.from.lowercase() == fromText &&
                                bus.to.lowercase() == toText &&
                                bus.date == dateText
                            ) {
                                val updatedBus = bus.copy(busId = busSnap.key ?: "")
                                busList.add(updatedBus)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("BusParseError", "Error parsing bus: ${e.message}")
                    }
                }

                if (busList.isEmpty()) {
                    Toast.makeText(this@FindBusActivity, "No buses found", Toast.LENGTH_SHORT).show()
                }

                busAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FindBusActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", error.toException().toString())
            }
        })
    }
}
