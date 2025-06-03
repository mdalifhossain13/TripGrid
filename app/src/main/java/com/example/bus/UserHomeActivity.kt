package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserHomeActivity : AppCompatActivity() {

    private lateinit var tvUserWelcome: TextView
    private lateinit var btnFindBus: Button
    private lateinit var btnMyBookings: Button
    private lateinit var btnLogout: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance()

        tvUserWelcome = findViewById(R.id.tvUserWelcome)
        btnFindBus = findViewById(R.id.btnFindBus)
        btnMyBookings = findViewById(R.id.btnMyBookings)
        btnLogout = findViewById(R.id.btnLogout)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = dbRef.getReference("users").child(userId)
            userRef.child("name").get().addOnSuccessListener {
                val name = it.value?.toString() ?: "User"
                tvUserWelcome.text = "Welcome, $name"
            }
        }

        btnFindBus.setOnClickListener {
            startActivity(Intent(this, FindBusActivity::class.java))
        }

        btnMyBookings.setOnClickListener {
            startActivity(Intent(this, MyBookingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, start::class.java)) // Replace with your actual start screen
            finish()
        }
    }
}
