package com.example.abssolutions

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import android.view.View
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Hide main content initially (this will be replaced by the login activity launch)
        findViewById<View>(R.id.main_content_layout).visibility = View.GONE

        // Post a delayed action to show main content and hide splash logo
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // Finish MainActivity so the user can't go back to the splash screen
            finish()
        }, 3000) // 3000 milliseconds = 3 seconds

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}