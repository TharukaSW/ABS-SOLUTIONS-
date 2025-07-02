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
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.Instant
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var reEnterPasswordEditText: EditText
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
        reEnterPasswordEditText = findViewById(R.id.editTextReEnterPasswordRegister)
        registerButton = findViewById(R.id.buttonRegister)

        // Set OnClickListener for the register button
        registerButton.setOnClickListener {
            saveUser()
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
    }

    private fun saveUser() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val reEnterPassword = reEnterPasswordEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || reEnterPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != reEnterPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Hardcoded/test values for ML model
        val height = 152
        val weight = 56
        val gender = 1 // 1 for male, 0 for female
        val bmiRate = 20.0

        // Call ML model API
        val url = "https://54642c9d-9064-4867-940f-fa3563c53456-00-1gpvcmxyxpo9x.sisko.replit.dev/predict"
        val requestQueue = Volley.newRequestQueue(this)
        val jsonBody = JSONObject()
        jsonBody.put("Height", height)
        jsonBody.put("Weight", weight)
        jsonBody.put("Gender", gender)
        jsonBody.put("BMIRate", bmiRate)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonBody,
            { response ->
                val bodyType = response.optString("prediction", "Unknown")
                registerUserWithBodyType(name, email, password, bodyType)
            },
            { error ->
                Toast.makeText(this, "ML API error: ${error.message}", Toast.LENGTH_SHORT).show()
                registerUserWithBodyType(name, email, password, "Unknown")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun registerUserWithBodyType(name: String, email: String, password: String, bodyType: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val joinDate = Instant.now().toString()
                    val memberData = mapOf(
                        "address" to "",
                        "allergies" to "",
                        "bmi" to "",
                        "bodyType" to bodyType,
                        "contact" to "",
                        "dob" to "",
                        "email" to email,
                        "firstName" to name,
                        "fitnessLevel" to "",
                        "gender" to "",
                        "height" to "",
                        "injuries" to "",
                        "joinDate" to joinDate,
                        "lastName" to "",
                        "medical" to "",
                        "medications" to "",
                        "notes" to "",
                        "type" to "normal",
                        "weight" to ""
                    )
                    val dbRef = FirebaseDatabase.getInstance().getReference("members")
                    dbRef.push().setValue(memberData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, PersonalHealthInfoActivity::class.java)
                            intent.putExtra("USER_EMAIL", email)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Registration failed: " + (task.exception?.message ?: "Unknown error"), Toast.LENGTH_SHORT).show()
                }
            }
    }
} 