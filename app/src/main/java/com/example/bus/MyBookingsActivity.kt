package com.example.bus

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val bookingList = mutableListOf<UserBookingModel>()
    private lateinit var adapter: UserBookingAdapter
    private val dbRef = FirebaseDatabase.getInstance().getReference("bookings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        recyclerView = findViewById(R.id.recyclerUserBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserBookingAdapter(bookingList)
        recyclerView.adapter = adapter

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId != null) {
            dbRef.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        bookingList.clear()
                        for (bookingSnap in snapshot.children) {
                            val booking = bookingSnap.getValue(UserBookingModel::class.java)
                            if (booking != null) {
                                bookingList.add(booking)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MyBookingsActivity, "Error loading bookings", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
