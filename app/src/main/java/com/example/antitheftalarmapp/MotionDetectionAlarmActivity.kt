package com.example.antitheftalarmapp

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.antitheftalarmapp.Services.SensorsAndWifiService
import kotlin.math.sqrt


class MotionDetectionAlarmActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var backIcon: ImageView
    private lateinit var animationView: LottieAnimationView
    private lateinit var activateCircle: ImageView
    private lateinit var pressActivateButtonTextView: TextView
    private lateinit var activatingPleaseWaitTextView: TextView
    private lateinit var activateTextView: TextView
    private lateinit var stopTextView: TextView
    private lateinit var stopCircle: ImageView
    private lateinit var flashCardView: ImageView
    private lateinit var vibrateCardView: ImageView

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var stopAlarmWithPinCodeSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var flashSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var vibrateSwitch: Switch

    private var isAnimationCompleted = false
    private var lastSensorValues = FloatArray(3)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private val SHARED_PREFS_FILE_NAME = "MySharedPrefs"
    private val SWITCH_STATE_KEY_FLASH = "switchStateFlash"
    private val SWITCH_STATE_KEY_VIBRATION = "switchStateVibrate"

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion_detection_alarm)

        // Inflate the layout XML file
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.activity_pin_code_settings, null) // Replace 'activity_main' with your layout file name


        mediaPlayer = MediaPlayer()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

        backIcon = findViewById(R.id.back_icon_3)
        animationView = findViewById(R.id.animation_view)
        activateCircle = findViewById(R.id.motion_detection_activation_circle)
        pressActivateButtonTextView = findViewById(R.id.press_activate_button_tv)
        stopAlarmWithPinCodeSwitch = layout.findViewById(R.id.stop_alarm_with_pin_code_card_switch)
        activatingPleaseWaitTextView = findViewById(R.id.activating_please_wait_tv)
        stopCircle = findViewById(R.id.motion_detection_activation_stop_circle)
        flashCardView = findViewById(R.id.flash_and_vibration_card_for_flash)
        vibrateCardView = findViewById(R.id.flash_and_vibration_card_for_vibration)
        flashSwitch = findViewById(R.id.flash_light_switch)
        vibrateSwitch = findViewById(R.id.vibration_switch)
        activateTextView = findViewById(R.id.activate_tv)
        stopTextView = findViewById(R.id.stop_tv)


        // Retrieve the switch state from SharedPreferences
        val sharedPreferencesSwitchState = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val switchState = sharedPreferencesSwitchState.getBoolean("switchState", false)

        flashSwitch.isChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY_FLASH, false)
        vibrateSwitch.isChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY_VIBRATION, false)


        flashSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state in SharedPreferences
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY_FLASH, isChecked).apply()

            // Implement your logic when the switch is toggled (e.g., start or stop alarm)
            if (isChecked && isAnimationCompleted) {
                startService()

            } /*else {
            }*/
        }

        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state in SharedPreferences
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY_VIBRATION, isChecked).apply()

            // Implement your logic when the switch is toggled (e.g., start or stop alarm)
            if (isChecked && isAnimationCompleted) {
                startService()

            } /*else {
            }*/
        }

        // Set the switch state in MotionDetectionAlarmActivity
        stopAlarmWithPinCodeSwitch.isChecked = switchState

        // Set the initial visibility to invisible
        animationView.visibility = View.INVISIBLE

        // Initialize accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Check if the accelerometer is available
        if (accelerometer == null) {
            // Handle the absence of accelerometer sensor
            // Display a message or take appropriate action
            // For example, you can show a toast message
            Toast.makeText(
                this,
                "Device does not have an accelerometer sensor",
                Toast.LENGTH_SHORT
            )
                .show()

        } else if (foregroundServiceRunning()) {
            activateCircle.visibility = View.INVISIBLE
            stopCircle.visibility = View.VISIBLE
            activateTextView.visibility = View.INVISIBLE
            stopTextView.visibility = View.VISIBLE

            stopCircle.setOnClickListener {
                stopMediaService()
                navigateToMainActivity()
                mediaPlayer.stop()
            }

        } else {
            activateCircle.setOnClickListener {
                startAnimation()
            }
        }
        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

        private fun startAnimation() {
            animationView.setAnimation("Animation - 1706164073705.json")
            activateCircle.visibility = View.INVISIBLE
            pressActivateButtonTextView.visibility = View.INVISIBLE
            activatingPleaseWaitTextView.visibility = View.VISIBLE
            activateTextView.visibility = View.INVISIBLE

            animationView.visibility = View.VISIBLE
            animationView.playAnimation()

            // Add a listener to detect when the animation completes
            animationView.addAnimatorListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(p0: Animator) {}

                @SuppressLint("SetTextI18n")
                override fun onAnimationEnd(p0: Animator) {
                    activateCircle.visibility = View.INVISIBLE
                    isAnimationCompleted = true


                    // Start listening to accelerometer sensor
                    sensorManager.registerListener(
                        this@MotionDetectionAlarmActivity,
                        accelerometer,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                    sensorManager.registerListener(
                        this@MotionDetectionAlarmActivity,
                        gyroscope,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )

                }

                override fun onAnimationCancel(p0: Animator) {}

                override fun onAnimationRepeat(p0: Animator) {}
            })
        }


        override fun onSensorChanged(event: SensorEvent) {
            val alpha = 0.2f

            // Apply low-pass filter to accelerometer data
            lastSensorValues[0] = alpha * lastSensorValues[0] + (1 - alpha) * event.values[0]
            lastSensorValues[1] = alpha * lastSensorValues[1] + (1 - alpha) * event.values[1]
            lastSensorValues[2] = alpha * lastSensorValues[2] + (1 - alpha) * event.values[2]

            // Check for device movement using filtered accelerometer data
            val x = lastSensorValues[0]
            val y = lastSensorValues[1]
            val z = lastSensorValues[2]
            Log.d("Accelerometer", "X: $x, Y: $y, Z: $z")

            // Customize the dynamic threshold based on the magnitude of acceleration
            val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val sensitiveThreshold = 11f

            // Use gyroscope data directly from the event
            val gyroscopeDataX = event.values[0]
            val gyroscopeDataY = event.values[1]
            val gyroscopeDataZ = event.values[2]

            // Customize the gyroscope threshold
            val gyroscopeThreshold = 10f

            // Check for movement based on both accelerometer and gyroscope data
            if (magnitude > sensitiveThreshold || gyroscopeDataX > gyroscopeThreshold || gyroscopeDataY > gyroscopeThreshold || gyroscopeDataZ > gyroscopeThreshold) {
                if (isAnimationCompleted && stopAlarmWithPinCodeSwitch.isChecked) {
                    // Both conditions are true, handle the case here
                    val intent = Intent(this@MotionDetectionAlarmActivity, StopAlarmWithPinCodeActivity::class.java)
                    startActivity(intent)
                    finish()
                    startService()
                } else {
                    val intent = Intent(this@MotionDetectionAlarmActivity, StopAlarmActivity::class.java)
                    startActivity(intent)
                    finish()
                    startService()
                }
            }
        }

            private fun startService() {
                if (!foregroundServiceRunning()) {
                    // Retrieve the selected duration from SharedPreferences
                    val selectedDuration =
                        sharedPreferences.getInt(getString(R.string.key_selected_duration), -1)

                    // Pass the volume setting and duration to the MediaService
                    val serviceIntent = Intent(this, SensorsAndWifiService::class.java)
                    serviceIntent.putExtra(SensorsAndWifiService.EXTRA_DURATION, selectedDuration)
                    startForegroundService(serviceIntent)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if needed
            }

            override fun onDestroy() {
                super.onDestroy()
                // Unregister accelerometer sensor when the activity is destroyed
                sensorManager.unregisterListener(this)
            }

            private fun foregroundServiceRunning(): Boolean {
                val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                    if (SensorsAndWifiService::class.java.name == service.service.className) {
                        return true
                    }
                }
                return false
            }

            private fun navigateToMainActivity() {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            private fun stopMediaService() {
                // Stop the MediaService
                val serviceIntent = Intent(this, SensorsAndWifiService::class.java)
                stopService(serviceIntent)
            }
}
