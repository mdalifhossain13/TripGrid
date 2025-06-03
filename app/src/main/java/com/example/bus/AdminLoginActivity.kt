package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.adminEmail)
        passwordInput = findViewById(R.id.adminPassword)
        loginButton = findViewById(R.id.btnAdminLogin)
        signupLink = findViewById(R.id.tvGoToSignup)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val dbRef = FirebaseDatabase.getInstance().getReference("admins").child(userId)

                        dbRef.get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, AdminHomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Not an admin account", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Database error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, AdminSignupActivity::class.java))
        }
    }
}
