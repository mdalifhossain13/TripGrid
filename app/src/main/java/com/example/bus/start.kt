package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class start : AppCompatActivity() {

    private lateinit var btnUserLogin: Button
    private lateinit var btnAdminLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        btnUserLogin = findViewById(R.id.btnUserLogin)
        btnAdminLogin = findViewById(R.id.btnAdminLogin)

        btnUserLogin.setOnClickListener {
            startActivity(Intent(this, UserLoginActivity::class.java))
        }

        btnAdminLogin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }
    }
}
