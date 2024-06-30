package com.example.antitheftalarmapp.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.antitheftalarmapp.MainActivity
import com.example.antitheftalarmapp.R

class BatteryDetectionService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val CHANNEL_ID = "Foreground Service Channel"
    private val NOTIFICATION_ID = 1
    private lateinit var vibrator: Vibrator
    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREFS_FILE_NAME = "MySharedPrefs"
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var isFlashlightOn = false
    private val SWITCH_STATE_KEY_FLASH = "switchStateFlash"
    private val SWITCH_STATE_KEY_VIBRATION = "switchStateVibrate"

    companion object {
        const val EXTRA_DURATION = "extra_duration"
    }


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = getCameraId()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Create a notification channel (if it hasn't been created already)
        val channel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        // Build the notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Charger Related Detection Services")
            .setContentText("Service is running in the background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        // Put the service in the foreground
        startForeground(NOTIFICATION_ID, notification)
        // Retrieve the duration from the intent
        val duration = intent?.getIntExtra(SensorsAndWifiService.EXTRA_DURATION, -1)

        if (duration != -1) {

            // Check if flashlight switch is toggled
            val isFlashlightEnabled = sharedPreferences.getBoolean(SWITCH_STATE_KEY_FLASH, false)
            // Check if vibration switch is toggled
            val isVibrationEnabled = sharedPreferences.getBoolean(SWITCH_STATE_KEY_VIBRATION, false)

            // Start the flashlight if it's not on already and the switch is toggled
            if (isFlashlightEnabled && !isFlashlightOn) {
                turnOnFlashlight()
            }

            // Start vibration if the switch is toggled
            if (isVibrationEnabled) {
                startVibration()
            }

            // Use the retrieved duration for your service logic
            // For example, you can set a timer to stop the service after the specified duration
            Handler(Looper.getMainLooper()).postDelayed({
                stopSelf()

                // Navigate to the MainActivity after the service stops
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainActivityIntent)

            }, duration!!.toLong() * 1000)
        }
        // Load the saved sound resource ID
        val selectedSoundResourceId = loadSavedSound()

// Check if a sound was previously selected
        if (selectedSoundResourceId != -1) {
            // Use the selectedSoundResourceId in your logic, for example, start a service
            playSound(selectedSoundResourceId)
        } else {
            // Handle the case where no sound was previously selected
            // You may choose to use a default sound or take another appropriate action
        }
        return START_STICKY
    }

    private fun startVibration() {
        Thread {
            val vibrationPattern = longArrayOf(0, 500, 500) // Vibrate for 0ms, wait for 500ms, vibrate for 500ms, and so on
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0)) }.start()
    }

    private fun stopVibration() {
        vibrator.cancel()
    }


    private fun turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true)
            isFlashlightOn = true
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to turn on flashlight", Toast.LENGTH_SHORT).show()
        }
    }

    private fun turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false)
            isFlashlightOn = false
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to turn off flashlight", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playSound(selectedSoundResourceId: Int) {
        // Stop and reset the current MediaPlayer instance
        mediaPlayer.stop()
        mediaPlayer.reset()

        // Set the new sound
        mediaPlayer = MediaPlayer.create(this, selectedSoundResourceId)

        // Set looping to play the sound continuously
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun loadSavedSound(): Int {
        return sharedPreferences.getInt(getString(R.string.key_selected_sound), -1)
    }
    private fun getCameraId(): String {
        val cameraIds = cameraManager.cameraIdList
        return cameraIds[0] // Use the first camera by default
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Charger Removal Detection Service Stopped", Toast.LENGTH_SHORT).show()
       // unregisterReceiver(batteryReceiver)
        // Stop vibration when the service is destroyed
        stopVibration()
        // Turn off the flashlight when the service is destroyed
        turnOffFlashlight()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent): IBinder? {
        // Not needed for this implementation
        return null
    }
}