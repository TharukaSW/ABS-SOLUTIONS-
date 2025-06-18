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
import com.example.abssolutions.data.UserDbHelper
import android.content.Intent

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

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

        // Get a writable database instance
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(UserContract.UserEntry.COLUMN_NAME_NAME, name)
            put(UserContract.UserEntry.COLUMN_NAME_EMAIL, email)
            put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, password)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(UserContract.UserEntry.TABLE_NAME, null, values)

        // Show a toast message indicating whether the insertion was successful
        if (newRowId == -1L) {
            Toast.makeText(this, "Error saving user. Email might already exist.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
            // Navigate to PersonalHealthInfoActivity
            val intent = Intent(this, PersonalHealthInfoActivity::class.java)
            intent.putExtra("USER_EMAIL", email) // Pass email to the next activity
            startActivity(intent)
            finish() // Finish RegisterActivity
        }

        // Close the database helper
        dbHelper.close()
    }
} 