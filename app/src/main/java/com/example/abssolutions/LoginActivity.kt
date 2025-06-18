package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.abssolutions.data.UserContract
import com.example.abssolutions.data.UserDbHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var textViewSignUp: TextView
    private lateinit var textViewCreateAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        textViewSignUp = findViewById(R.id.textViewSignUp)
        textViewCreateAccount = findViewById(R.id.textViewCreateAccount)

        // Set OnClickListener for the login button
        loginButton.setOnClickListener {
            loginUser()
        }

        // Set OnClickListener for the "Sign Up" TextView
        textViewSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for the "Create new Account" TextView
        textViewCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a readable database instance
        val dbHelper = UserDbHelper(this)
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database you will actually use after this query.
        val projection = arrayOf(
            UserContract.UserEntry.COLUMN_NAME_NAME,
            UserContract.UserEntry.COLUMN_NAME_EMAIL // Include email in projection
        )

        // Filter results WHERE "email" = 'entered_email' AND "password" = 'entered_password'
        val selection = "${UserContract.UserEntry.COLUMN_NAME_EMAIL} = ? AND ${UserContract.UserEntry.COLUMN_NAME_PASSWORD} = ?"
        val selectionArgs = arrayOf(email, password)

        val cursor = db.query(
            UserContract.UserEntry.TABLE_NAME,   // The table to query
            projection,         // The array of columns to return (null to return all)
            selection,          // The columns for the WHERE clause
            selectionArgs,      // The values for the WHERE clause
            null,               // don't group the rows
            null,               // don't filter by row groups
            null           // The sort order
        )

        with(cursor) {
            if (moveToFirst()) {
                // User found, login successful
                Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                // Get user details from the cursor
                val userName = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_NAME))
                val userEmail = getString(getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_EMAIL))

                // Navigate to UserProfileActivity and pass user data
                val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
                intent.putExtra("USER_NAME", userName)
                intent.putExtra("USER_EMAIL", userEmail)
                startActivity(intent)
                finish() // Finish LoginActivity

            } else {
                // User not found or incorrect credentials
                Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
        cursor.close()
        dbHelper.close()
    }
} 