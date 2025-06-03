package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminSignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: FirebaseDatabase

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_signup)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance()

        // Find Views
        nameInput = findViewById(R.id.signupName)
        emailInput = findViewById(R.id.signupEmail)
        passwordInput = findViewById(R.id.signupPassword)
        signupButton = findViewById(R.id.btnSignup)

        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create account in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser!!.uid
                        val adminData = mapOf(
                            "name" to name,
                            "email" to email
                        )
                        // Save admin info in Realtime Database
                        dbRef.getReference("admins").child(userId).setValue(adminData)

                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AdminLoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
