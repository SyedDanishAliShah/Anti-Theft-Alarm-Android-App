package com.example.antitheftalarmapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.antitheftalarmapp.Services.BatteryDetectionService
import com.example.antitheftalarmapp.Services.SensorsAndWifiService

class StopAlarmActivity : AppCompatActivity() {

    private lateinit var greenCircle: ImageView

    private var isColorChanging = false

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var sharedPreferences : SharedPreferences

    private val SHARED_PREFS_FILE_NAME = "MySharedPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_motion_detection_alarm)

        mediaPlayer = MediaPlayer()


       // greenCircle = findViewById(R.id.green_circle)

        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

        // Start changing background color in a loop
        changeBackgroundColorInLoop()

        greenCircle = findViewById(R.id.stop_circle)


        // Set OnClickListener for the green circle ImageView
        greenCircle.setOnClickListener {
            stopMediaService()
            navigateToMainActivity()
            mediaPlayer.stop()
        }


    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Check if the KeyEvent is a volume up or down event
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Consume the volume key events without taking any action
            return true
        }
        // If not volume up or down, or not navigating to StopMotionDetectionAlarmActivity, proceed with normal event handling
        return super.dispatchKeyEvent(event)
    }

    private fun stopMediaService() {
        // Stop the BatteryDetectionService
        val batteryServiceIntent = Intent(this, BatteryDetectionService::class.java)
        stopService(batteryServiceIntent)

        // Stop the SensorsService
        val sensorsServiceIntent = Intent(this, SensorsAndWifiService::class.java)
        stopService(sensorsServiceIntent)
    }

    private fun changeBackgroundColorInLoop() {
        isColorChanging = true
        val handler = Handler()
        val delay: Long = 500 // half-second delay

        handler.postDelayed(object : Runnable {
            var isRed = true

            override fun run() {
                if (isColorChanging) {
                    // Change background color from red to green and vice versa
                    if (isRed) {
                        window.decorView.setBackgroundColor(Color.GREEN)
                    } else {
                        window.decorView.setBackgroundColor(Color.RED)
                    }

                    isRed = !isRed

                    // Repeat the color change
                    handler.postDelayed(this, delay)
                }
            }
        }, delay)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
       mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        isColorChanging = false
    }


}