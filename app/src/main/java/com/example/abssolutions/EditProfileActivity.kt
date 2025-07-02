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
import android.view.View
import android.widget.LinearLayout
import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editTextEditName: EditText
    private lateinit var editTextEditGender: EditText
    private lateinit var editTextEditWeight: EditText
    private lateinit var editTextEditHeight: EditText
    private lateinit var editTextEditBMIRate: EditText
    private lateinit var editTextEditBirthDate: EditText
    private lateinit var editTextEditEmail: EditText
    private lateinit var editTextEditAddress: EditText
    private lateinit var editTextEditContactNo: EditText
    private lateinit var editTextEditMedicalConditions: EditText
    private lateinit var editTextEditInjuries: EditText
    private lateinit var editTextEditAllergies: EditText
    private lateinit var editTextEditCurrentMedications: EditText
    private lateinit var editTextEditAdditionalNotes: EditText
    private lateinit var buttonSaveProfile: Button
    private lateinit var editHealthInfoLayout: LinearLayout
    private lateinit var imageViewEditProfile: ImageView
    private lateinit var buttonChangePicture: Button
    private var selectedImageUri: Uri? = null
    private var profileImageBase64: String? = null
    private val PICK_IMAGE_REQUEST = 101

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Get user email from intent
        userEmail = intent.getStringExtra("USER_EMAIL")

        // Initialize views
        editTextEditName = findViewById(R.id.editTextEditName)
        editTextEditGender = findViewById(R.id.editTextEditGender)
        editTextEditWeight = findViewById(R.id.editTextEditWeight)
        editTextEditHeight = findViewById(R.id.editTextEditHeight)
        editTextEditBMIRate = findViewById(R.id.editTextEditBMIRate)
        editTextEditBirthDate = findViewById(R.id.editTextEditBirthDate)
        editTextEditEmail = findViewById(R.id.editTextEditEmail)
        editTextEditAddress = findViewById(R.id.editTextEditAddress)
        editTextEditContactNo = findViewById(R.id.editTextEditContactNo)
        editTextEditMedicalConditions = findViewById(R.id.editTextEditMedicalConditions)
        editTextEditInjuries = findViewById(R.id.editTextEditInjuries)
        editTextEditAllergies = findViewById(R.id.editTextEditAllergies)
        editTextEditCurrentMedications = findViewById(R.id.editTextEditCurrentMedications)
        editTextEditAdditionalNotes = findViewById(R.id.editTextEditAdditionalNotes)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)
        editHealthInfoLayout = findViewById(R.id.editHealthInfoLayout)
        imageViewEditProfile = findViewById(R.id.imageViewEditProfile)
        buttonChangePicture = findViewById(R.id.buttonChangePicture)

        // Load and display existing user data
        val email = userEmail // Create a local immutable copy
        if (email != null) {
            loadUserProfile(email)
        } else {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            finish() // Close activity if email is missing
        }

        // Set OnClickListener for the Save button
        buttonSaveProfile.setOnClickListener {
            saveUserProfile()
        }

        buttonChangePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_background)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageViewEditProfile.setImageBitmap(bitmap)
            // Convert to Base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val imageBytes = baos.toByteArray()
            profileImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
    }

    private fun loadUserProfile(email: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("members")
        dbRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val member = snapshot.children.first()
                        val memberData = member.value as? Map<*, *>
                        editTextEditName.setText(memberData?.get("firstName") as? String ?: "")
                        editTextEditGender.setText(memberData?.get("gender") as? String ?: "")
                        editTextEditWeight.setText(memberData?.get("weight") as? String ?: "")
                        editTextEditHeight.setText(memberData?.get("height") as? String ?: "")
                        editTextEditBMIRate.setText(memberData?.get("bmi") as? String ?: "")
                        editTextEditBirthDate.setText(memberData?.get("dob") as? String ?: "")
                        editTextEditEmail.setText(memberData?.get("email") as? String ?: email)
                        editTextEditAddress.setText(memberData?.get("address") as? String ?: "")
                        editTextEditContactNo.setText(memberData?.get("contact") as? String ?: "")
                        editTextEditMedicalConditions.setText(memberData?.get("medical") as? String ?: "")
                        editTextEditInjuries.setText(memberData?.get("injuries") as? String ?: "")
                        editTextEditAllergies.setText(memberData?.get("allergies") as? String ?: "")
                        editTextEditCurrentMedications.setText(memberData?.get("medications") as? String ?: "")
                        editTextEditAdditionalNotes.setText(memberData?.get("notes") as? String ?: "")
                        // Optionally load profile image if you store a URL or base64
                        // If you use base64, decode and set as bitmap
                        // Example for base64 (uncomment if needed):
                        // val profileImageBase64 = memberData?.get("profileImageUrl") as? String
                        // if (!profileImageBase64.isNullOrEmpty()) {
                        //     val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                        //     val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        //     imageViewEditProfile.setImageBitmap(bitmap)
                        // }
                    } else {
                        Toast.makeText(this@EditProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditProfileActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveUserProfile() {
        val name = editTextEditName.text.toString().trim()
        val gender = editTextEditGender.text.toString().trim()
        val weight = editTextEditWeight.text.toString().trim()
        val height = editTextEditHeight.text.toString().trim()
        val bmiRate = editTextEditBMIRate.text.toString().trim()
        val birthDate = editTextEditBirthDate.text.toString().trim()
        val email = editTextEditEmail.text.toString().trim()
        val address = editTextEditAddress.text.toString().trim()
        val contactNo = editTextEditContactNo.text.toString().trim()
        val medicalConditions = editTextEditMedicalConditions.text.toString().trim()
        val injuries = editTextEditInjuries.text.toString().trim()
        val allergies = editTextEditAllergies.text.toString().trim()
        val currentMedications = editTextEditCurrentMedications.text.toString().trim()
        val additionalNotes = editTextEditAdditionalNotes.text.toString().trim()

        // Basic validation
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare user profile update map matching the member structure
        val profileMap = mutableMapOf<String, Any?>(
            "firstName" to name,
            "gender" to gender,
            "weight" to weight,
            "height" to height,
            "bmi" to bmiRate,
            "dob" to birthDate,
            "email" to email,
            "address" to address,
            "contact" to contactNo,
            "medical" to medicalConditions,
            "injuries" to injuries,
            "allergies" to allergies,
            "medications" to currentMedications,
            "notes" to additionalNotes
        )

        fun updateProfileInDatabase(profileImageUrl: String? = null) {
            if (!profileImageUrl.isNullOrEmpty()) {
                profileMap["profileImageUrl"] = profileImageUrl
            }
            val dbRef = FirebaseDatabase.getInstance().getReference("members")
            dbRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val memberKey = snapshot.children.first().key
                            if (memberKey != null) {
                                dbRef.child(memberKey).updateChildren(profileMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this@EditProfileActivity, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this@EditProfileActivity, "Member not found in database", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@EditProfileActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // If a new image is selected, upload to Firebase Storage
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${email.replace("@", "_").replace(".", "_")}.jpg")
            val uploadTask = storageRef.putFile(selectedImageUri!!)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updateProfileInDatabase(downloadUri.toString())
                } else {
                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                    updateProfileInDatabase()
                }
            }
        } else {
            updateProfileInDatabase()
        }
    }
} 