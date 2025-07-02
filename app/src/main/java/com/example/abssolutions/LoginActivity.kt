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
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Login user with Firebase Authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch user data from Realtime Database
                    val dbRef = FirebaseDatabase.getInstance().getReference("members")
                    dbRef.orderByChild("email").equalTo(email)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // Get the first matching member
                                    val member = snapshot.children.first()
                                    val memberData = member.value as? Map<*, *>
                                    val firstName = memberData?.get("firstName") as? String ?: ""
                                    val lastName = memberData?.get("lastName") as? String ?: ""
                                    val userEmail = memberData?.get("email") as? String ?: email
                                    // Pass user info to UserProfileActivity
                                    val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
                                    intent.putExtra("USER_NAME", "$firstName $lastName")
                                    intent.putExtra("USER_EMAIL", userEmail)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "User data not found in database", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@LoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: " + (task.exception?.message ?: "Unknown error"), Toast.LENGTH_SHORT).show()
                }
            }
    }
} 