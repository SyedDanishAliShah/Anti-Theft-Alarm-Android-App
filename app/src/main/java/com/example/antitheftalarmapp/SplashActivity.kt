package com.example.antitheftalarmapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Set click listener for the "Let's Go" button using findViewById
        val letsGoButton = findViewById<ImageView>(R.id.letsGoButton)

        // Set click listener for the "Let's Go" button
        letsGoButton.setOnClickListener {
            // Create an Intent to open the YourNewActivity
            val intent = Intent(this, MainActivity::class.java)

            // Start the new activity
            startActivity(intent)
        }
    }
}
