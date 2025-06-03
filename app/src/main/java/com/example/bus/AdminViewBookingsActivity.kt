package com.example.bus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminViewBookingsActivity : AppCompatActivity() {

    private lateinit var recyclerBookings: RecyclerView
    private lateinit var bookingList: ArrayList<BookingModel>
    private lateinit var adapter: AdminBookingAdapter
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_bookings)

        recyclerBookings = findViewById(R.id.recyclerBookings)
        recyclerBookings.layoutManager = LinearLayoutManager(this)
        bookingList = ArrayList()
        adapter = AdminBookingAdapter(bookingList)
        recyclerBookings.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("bookings")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList.clear()
                for (bookingSnap in snapshot.children) {
                    val booking = bookingSnap.getValue(BookingModel::class.java)
                    if (booking != null) {
                        bookingList.add(booking)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
