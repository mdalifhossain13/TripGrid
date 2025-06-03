package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class UserLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.loginEmail)
        passwordInput = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.btnUserLogin)
        signupLink = findViewById(R.id.tvGoToSignup)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, UserHomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, UserSignupActivity::class.java))
        }
    }
}
