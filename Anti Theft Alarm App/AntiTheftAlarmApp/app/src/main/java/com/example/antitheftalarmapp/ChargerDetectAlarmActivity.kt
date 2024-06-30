package com.example.antitheftalarmapp

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.antitheftalarmapp.Services.BatteryDetectionService

class ChargerDetectAlarmActivity : AppCompatActivity() {

    private lateinit var backIcon: ImageView
    private lateinit var animationView: LottieAnimationView
    private lateinit var activateCircle: ImageView
    private lateinit var activateTextView: TextView
    private lateinit var pressActivateButtonTextView: TextView
    private lateinit var activatingPleaseWaitTextView: TextView
    private lateinit var stopCircle : ImageView
    private lateinit var stopTextView : TextView
    private lateinit var flashCardView: ImageView
    private lateinit var vibrateCardView: ImageView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var stopAlarmWithPinCodeSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var flashSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var vibrateSwitch: Switch

    private var isAnimationCompleted = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private val SHARED_PREFS_FILE_NAME = "MySharedPrefs"
    private val SWITCH_STATE_KEY_FLASH = "switchStateFlash"
    private val SWITCH_STATE_KEY_VIBRATION = "switchStateVibrate"
    private var wasCharging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charger_detect_alarm)

        val inflater = LayoutInflater.from(this)
        val inflatedLayout = inflater.inflate(R.layout.activity_pin_code_settings, null)

        mediaPlayer = MediaPlayer()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

        backIcon = findViewById(R.id.back_icon_3_charger_detect)
        animationView = findViewById(R.id.animation_view_charger_detect)
        activateCircle = findViewById(R.id.motion_detection_activation_circle_charger_detect)
        stopAlarmWithPinCodeSwitch = inflatedLayout.findViewById(R.id.stop_alarm_with_pin_code_card_switch)
        flashCardView = findViewById(R.id.flash_and_vibration_card_for_flash_charger_detect)
        vibrateCardView = findViewById(R.id.flash_and_vibration_card_for_vibration_charger_detect)
       flashSwitch = findViewById(R.id.flash_light_switch_charger_detect)
        vibrateSwitch = findViewById(R.id.vibration_switch_charger_detect)
        pressActivateButtonTextView = findViewById(R.id.press_activate_button_tv_charger_detect)
        activatingPleaseWaitTextView = findViewById(R.id.activating_please_wait_tv_charger_detect)
        activateTextView = findViewById(R.id.activate_tv_charger_detect)
        stopCircle = findViewById(R.id.charger_detect_activation_stop_circle)
        stopTextView = findViewById(R.id.stop_tv_charger_detect)


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

        stopAlarmWithPinCodeSwitch.isChecked = switchState

        // Set the initial visibility to invisible
        animationView.visibility = View.INVISIBLE

     /*   redCircle = findViewById(R.id.red_circle_2)
*/
        if (foregroundServiceRunning()){
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
        activateTextView.visibility = View.INVISIBLE
        pressActivateButtonTextView.visibility = View.INVISIBLE
        activatingPleaseWaitTextView.visibility = View.VISIBLE
        animationView.visibility = View.VISIBLE
        animationView.playAnimation()

        // Listen for changes in charging status
        val batteryStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Update charging status
                updateChargingStatus()
            }
        }

        // Register the receiver dynamically
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStatusReceiver, filter)

        // Add a listener to detect when the animation completes
        animationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {}

            @SuppressLint("SetTextI18n")
            override fun onAnimationEnd(p0: Animator) {

                activateCircle.visibility = View.INVISIBLE
                isAnimationCompleted = true

                // Get the current charging status
                val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                    applicationContext.registerReceiver(null, ifilter)
                }
                val status: Int? = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

                // Now you can use updated conditions by passing the charging status
                handleConditions(isCharging)

            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}
        })

        }

    private fun updateChargingStatus() {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }

        val status: Int? = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

        // Handle conditions after charging status update
        handleConditions(isCharging)
    }

    private fun handleConditions(isCharging: Boolean) {
        // Check if charging status changed from charging to not charging
        if (wasCharging && !isCharging && isAnimationCompleted && stopAlarmWithPinCodeSwitch.isChecked) {
            // All conditions are true, handle the case here
            val intent = Intent(this@ChargerDetectAlarmActivity, StopAlarmWithPinCodeActivity::class.java)
            startActivity(intent)
            finish()
            startService()
        }
        else if (wasCharging && !isCharging &&isAnimationCompleted && !stopAlarmWithPinCodeSwitch.isChecked){
            val intent = Intent(this@ChargerDetectAlarmActivity, StopAlarmActivity::class.java)
            startActivity(intent)
            finish()
            startService()
        }
        // Update the charging status for the next iteration
        wasCharging = isCharging
    }

    private fun startService() {
        if (!foregroundServiceRunning()) {
            // Retrieve the selected duration from SharedPreferences
            val selectedDuration = sharedPreferences.getInt(getString(R.string.key_selected_duration), -1)

            val serviceIntent = Intent(this, BatteryDetectionService::class.java)
            serviceIntent.putExtra(BatteryDetectionService.EXTRA_DURATION, selectedDuration)
            startForegroundService(serviceIntent)
        }
    }
    private fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BatteryDetectionService::class.java.name == service.service.className) {
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
        val serviceIntent = Intent(this, BatteryDetectionService::class.java)
        stopService(serviceIntent)
    }


    }
