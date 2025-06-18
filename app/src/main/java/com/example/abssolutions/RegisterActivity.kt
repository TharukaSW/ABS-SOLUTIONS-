package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.ContentValues
import com.example.abssolutions.data.UserContract
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        nameEditText = findViewById(R.id.editTextTextPersonName)
        emailEditText = findViewById(R.id.editTextTextEmailAddressRegister)
        passwordEditText = findViewById(R.id.editTextTextPasswordRegister)
        registerButton = findViewById(R.id.buttonRegister)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Set OnClickListener for the register button
        registerButton.setOnClickListener {
            saveUser()
        }
    }

    private fun saveUser() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Save user to Firebase Realtime Database
        val userMap = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )
        // Use email as key (replace . with , to avoid Firebase key issues)
        val userKey = email.replace('.', ',')
        database.child("users").child(userKey).setValue(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PersonalHealthInfoActivity::class.java)
                intent.putExtra("USER_EMAIL", email)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error saving user. Email might already exist.", Toast.LENGTH_SHORT).show()
            }
    }
} 