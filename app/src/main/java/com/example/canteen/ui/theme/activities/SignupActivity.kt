package com.example.canteen.ui.theme.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.canteen.R

class SignupActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // ✅ Correct IDs (must match XML)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnSignup = findViewById(R.id.btnAction)

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)

        btnSignup.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // ✅ Empty validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Password validation
            if (!isValidPassword(password)) {
                Toast.makeText(
                    this,
                    "Password must be 8+ chars, include uppercase, lowercase, digit & special char",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // ✅ Save data (IMPORTANT: username, not email)
            val editor = sharedPref.edit()
            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()

            Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()

            finish() // go back to login
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
        return regex.matches(password)
    }
}