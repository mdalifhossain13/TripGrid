package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnAddBus: Button
    private lateinit var btnViewBookings: Button
    private lateinit var btnLogout: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        tvWelcome = findViewById(R.id.tvWelcome)
        btnAddBus = findViewById(R.id.btnAddBus)
        btnViewBookings = findViewById(R.id.btnViewBookings)
        btnLogout = findViewById(R.id.btnLogout) // new logout button

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            val adminRef = database.getReference("admins").child(currentUserId)
            adminRef.child("name").get().addOnSuccessListener {
                val name = it.value?.toString() ?: "Admin"
                tvWelcome.text = "Welcome, $name"
            }
        }

        btnAddBus.setOnClickListener {
            startActivity(Intent(this, AddBusActivity::class.java))
        }

        btnViewBookings.setOnClickListener {
            startActivity(Intent(this, AdminViewBookingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, start::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
