package com.example.antitheftalarmapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.antitheftalarmapp.Services.BatteryDetectionService
import com.example.antitheftalarmapp.Services.CapturePhotoForegroundService
import com.example.antitheftalarmapp.Services.SensorsAndWifiService
import com.example.antitheftalarmapp.objectclasses.PinHolder

class StopAlarmWithPinCodeActivity :  AppCompatActivity(), View.OnClickListener {

    private var isColorChanging = false
    private var enteredPin = ""
    private var isPinEntered = false
    private var incorrectAttempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_alarm_with_pin_code)

        changeBackgroundColorInLoop()

        val numberTextViews: Array<TextView> = arrayOf(
            findViewById(R.id.one_1), findViewById(R.id.two_1), findViewById(R.id.three_1),
            findViewById(R.id.four_1), findViewById(R.id.five_1), findViewById(R.id.six_1),
            findViewById(R.id.seven_1), findViewById(R.id.eight_1), findViewById(R.id.nine_1),
            findViewById(R.id.zero_1)
        )

        for (numberTextView in numberTextViews) {
            numberTextView.setOnClickListener(this)
        }

        val backSpace: ImageView = findViewById(R.id.back_space_for_enter_pin_1)
        backSpace.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.substring(0, enteredPin.length - 1)
                updateEnterPinCircles(enteredPin.length)
            }
        }

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

    override fun onClick(view: View?) {
        if (view is TextView && enteredPin.length < 4) {
            val number = view.text.toString()
            enteredPin += number
            updateEnterPinCircles(enteredPin.length)

            if (enteredPin.length == 4) {
                // Get the PinHolder instance in your activity
                val pinHolder = PinHolder.getInstance(applicationContext)
                val savedPin = pinHolder.enteredPin

                if (enteredPin == savedPin) {
                    // Correct pin entered, navigate to MainActivity and stop the service
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    stopMediaService()
                } else {
                    // Incorrect pin entered, show a toast, reset the entered pin, and increment incorrectAttempts
                    incorrectAttempts++
                    Toast.makeText(this, "Invalid Pin. Please try again.", Toast.LENGTH_LONG).show()
                    enteredPin = ""
                    updateEnterPinCircles(0)

                    // Check if the maximum attempts limit is reached
                    if (incorrectAttempts >= 3) {
                        CapturePhotoForegroundService.requestCameraPermission(this)
                        val serviceIntent = Intent(this, CapturePhotoForegroundService::class.java)
                        startForegroundService(serviceIntent)
                    }
                }
            }
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



    private fun updateEnterPinCircles(enteredDigits: Int) {
        val enterCircles: Array<ImageView> = arrayOf(
            findViewById(R.id.enter_password_circle_4),
            findViewById(R.id.enter_password_circle_5),
            findViewById(R.id.enter_password_circle_6),
            findViewById(R.id.enter_password_circle_7)
        )

        for (i in enterCircles.indices) {
            if (i < enteredDigits) {
                enterCircles[i].setImageResource(R.drawable.enter_pin_filled_colored_circle)
            } else {
                if (!isPinEntered) {
                    enterCircles[i].setImageResource(R.drawable.change_pin_code_circle)
                }
            }
        }
    }
    private fun stopMediaService() {
        // Stop the BatteryDetectionService
        val batteryServiceIntent = Intent(this, BatteryDetectionService::class.java)
        stopService(batteryServiceIntent)

        // Stop the SensorsService
        val sensorsServiceIntent = Intent(this, SensorsAndWifiService::class.java)
        stopService(sensorsServiceIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterReceiver(broadcastReceiver)
        isColorChanging = false
    }

}
