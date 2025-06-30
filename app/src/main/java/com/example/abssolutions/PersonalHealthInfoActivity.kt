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
import android.widget.CheckBox
import android.widget.LinearLayout
import android.view.View
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.util.Log

class PersonalHealthInfoActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextGender: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var editTextBMIRate: EditText
    private lateinit var editTextBirthDate: EditText
    private lateinit var editTextEmailInfo: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextContactNo: EditText
    private lateinit var editTextMedicalConditions: EditText
    private lateinit var editTextInjuries: EditText
    private lateinit var editTextAllergies: EditText
    private lateinit var editTextCurrentMedications: EditText
    private lateinit var editTextAdditionalNotes: EditText
    private lateinit var checkBoxAgreePrivacy: CheckBox
    private lateinit var buttonContinue: Button
    private lateinit var buttonMember: Button
    private lateinit var buttonPatient: Button
    private lateinit var healthInfoLayout: LinearLayout

    private var userEmail: String? = null
    private var currentUserType: String = "Member" // Default user type
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_health_info)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personal_health_info_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get email from intent (passed from RegisterActivity)
        userEmail = intent.getStringExtra("USER_EMAIL")

        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextGender = findViewById(R.id.editTextGender)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextBMIRate = findViewById(R.id.editTextBMIRate)
        editTextBirthDate = findViewById(R.id.editTextBirthDate)
        editTextEmailInfo = findViewById(R.id.editTextEmailInfo)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextContactNo = findViewById(R.id.editTextContactNo)
        editTextMedicalConditions = findViewById(R.id.editTextMedicalConditions)
        editTextInjuries = findViewById(R.id.editTextInjuries)
        editTextAllergies = findViewById(R.id.editTextAllergies)
        editTextCurrentMedications = findViewById(R.id.editTextCurrentMedications)
        editTextAdditionalNotes = findViewById(R.id.editTextAdditionalNotes)
        checkBoxAgreePrivacy = findViewById(R.id.checkBoxAgreePrivacy)
        buttonContinue = findViewById(R.id.buttonContinue)
        buttonMember = findViewById(R.id.buttonMember)
        buttonPatient = findViewById(R.id.buttonPatient)
        healthInfoLayout = findViewById(R.id.healthInfoLayout)

        // Pre-fill email if available
        editTextEmailInfo.setText(userEmail)
        editTextEmailInfo.isEnabled = false // Prevent editing email

        // Set initial state of the toggle and health info section
        setToggleState("Member")

        // Set OnClickListener for the toggle buttons
        buttonMember.setOnClickListener { setToggleState("Member") }
        buttonPatient.setOnClickListener { setToggleState("Patient") }

        // Set OnClickListener for the Continue button
        buttonContinue.setOnClickListener {
            savePersonalHealthInfo()
        }

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference
    }

    private fun setToggleState(userType: String) {
        currentUserType = userType
        if (userType == "Member") {
            buttonMember.setBackgroundResource(R.drawable.toggle_left_selected)
            buttonMember.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            buttonPatient.setBackgroundResource(R.drawable.toggle_right_unselected)
            buttonPatient.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            healthInfoLayout.visibility = View.GONE
        } else {
            buttonMember.setBackgroundResource(R.drawable.toggle_left_unselected)
            buttonMember.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            buttonPatient.setBackgroundResource(R.drawable.toggle_right_selected)
            buttonPatient.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            healthInfoLayout.visibility = View.VISIBLE
        }
    }

    private fun savePersonalHealthInfo() {
        val name = editTextName.text.toString().trim()
        val gender = editTextGender.text.toString().trim()
        val weight = editTextWeight.text.toString().trim()
        val height = editTextHeight.text.toString().trim()
        val bmiRate = editTextBMIRate.text.toString().trim()
        val birthDate = editTextBirthDate.text.toString().trim()
        val email = editTextEmailInfo.text.toString().trim()
        val address = editTextAddress.text.toString().trim()
        val contactNo = editTextContactNo.text.toString().trim()
        val medicalConditions = editTextMedicalConditions.text.toString().trim()
        val injuries = editTextInjuries.text.toString().trim()
        val allergies = editTextAllergies.text.toString().trim()
        val currentMedications = editTextCurrentMedications.text.toString().trim()
        val additionalNotes = editTextAdditionalNotes.text.toString().trim()

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || !checkBoxAgreePrivacy.isChecked) {
            Toast.makeText(this, "Please fill required fields and agree to privacy policy", Toast.LENGTH_SHORT).show()
            return
        }

        // Call ML model to get body type
        predictBodyType(height, weight, gender, bmiRate) { bodyType ->
            // Prepare user health info map
            val healthInfoMap = mutableMapOf<String, Any?>(
                "name" to name,
                "user_type" to currentUserType,
                "gender" to gender,
                "weight" to (weight.toDoubleOrNull() ?: 0.0),
                "height" to (height.toDoubleOrNull() ?: 0.0),
                "bmi_rate" to (bmiRate.toDoubleOrNull() ?: 0.0),
                "birth_date" to birthDate,
                "address" to address,
                "contact_no" to contactNo,
                "body_type" to bodyType
            )
            if (currentUserType == "Patient") {
                healthInfoMap["medical_conditions"] = medicalConditions
                healthInfoMap["injuries"] = injuries
                healthInfoMap["allergies"] = allergies
                healthInfoMap["current_medications"] = currentMedications
                healthInfoMap["additional_notes"] = additionalNotes
            } else {
                healthInfoMap["medical_conditions"] = ""
                healthInfoMap["injuries"] = ""
                healthInfoMap["allergies"] = ""
                healthInfoMap["current_medications"] = ""
                healthInfoMap["additional_notes"] = ""
            }
            // Use email as key (replace . with , to avoid Firebase key issues)
            val userKey = email.replace('.', ',')
            database.child("users").child(userKey).updateChildren(healthInfoMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    // Redirect to login page
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating profile or email not found", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun predictBodyType(height: String, weight: String, gender: String, bmiRate: String, callback: (String) -> Unit) {
        val url = "https://54642c9d-9064-4867-940f-fa3563c53456-00-1gpvcmxyxpo9x.sisko.replit.dev/predict"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("Height", height.toDoubleOrNull() ?: 0.0)
        jsonBody.put("Weight", weight.toDoubleOrNull() ?: 0.0)
        val genderValue = if (gender.equals("male", ignoreCase = true)) 1 else 0
        jsonBody.put("Gender", genderValue)
        jsonBody.put("BMIRate", bmiRate.toDoubleOrNull() ?: 0.0)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonBody,
            { response ->
                Log.d("API_RESPONSE", "Full response: $response")
                val bodyType = response.optString("prediction", "Unknown")
                callback(bodyType)
            },
            { error ->
                Log.e("API_ERROR", "Error: ${error.message}", error)
                Toast.makeText(this, "API Error: ${error.message}", Toast.LENGTH_SHORT).show()
                callback("Error") // Handle error case
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
} 