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
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.antitheftalarmapp.Services.SensorsAndWifiService

class WifiChangeDetectAlarmActivity : AppCompatActivity() {

    private lateinit var backIcon: ImageView
    private lateinit var animationView: LottieAnimationView
    private lateinit var activateCircle: ImageView
    private lateinit var activateTextView: TextView
    private lateinit var pressActivateButtonTextView: TextView
    private lateinit var activatingPleaseWaitTextView: TextView
    private lateinit var flashCardView: ImageView
    private lateinit var stopCircle : ImageView
    private lateinit var stopTextView : TextView
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
    private lateinit var wifiStateReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the broadcast receiver
        wifiStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiState = wifiManager.wifiState

                    // Check if WiFi is enabled or disabled
                    val isWifiEnabled = wifiState == WifiManager.WIFI_STATE_ENABLED
                    val isWifiDisabled = wifiState == WifiManager.WIFI_STATE_DISABLED

                    // Handle WiFi state changes here
                    if (isWifiEnabled && isAnimationCompleted && !stopAlarmWithPinCodeSwitch.isChecked) {

                        val intent = Intent(this@WifiChangeDetectAlarmActivity, StopAlarmActivity::class.java)
                        startActivity(intent)
                        finish()
                        startService()

                    } else if (isWifiEnabled && isAnimationCompleted && stopAlarmWithPinCodeSwitch.isChecked) {

                        val intent = Intent(this@WifiChangeDetectAlarmActivity, StopAlarmWithPinCodeActivity::class.java)
                        startActivity(intent)
                        finish()
                        startService()

                    }
                    else if (isWifiDisabled && isAnimationCompleted && !stopAlarmWithPinCodeSwitch.isChecked){

                        val intent = Intent(this@WifiChangeDetectAlarmActivity, StopAlarmActivity::class.java)
                        startActivity(intent)
                        finish()
                        startService()
                    }

                    else if (isWifiDisabled && isAnimationCompleted && stopAlarmWithPinCodeSwitch.isChecked){

                        val intent = Intent(this@WifiChangeDetectAlarmActivity, StopAlarmWithPinCodeActivity::class.java)
                        startActivity(intent)
                        finish()
                        startService()
                    }
                }
            }
        }

        registerReceiver(wifiStateReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))

        setContentView(R.layout.activity_wifi_state_changed_detect)

        val inflater = LayoutInflater.from(this)

        val inflatedLayout = inflater.inflate(R.layout.activity_pin_code_settings, null)

        mediaPlayer = MediaPlayer()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

        backIcon = findViewById(R.id.back_icon_3_wifi_state)
        animationView = findViewById(R.id.animation_view_wifi_state)
        activateCircle = findViewById(R.id.motion_detection_activation_circle_wifi_state)
        stopAlarmWithPinCodeSwitch = inflatedLayout.findViewById(R.id.stop_alarm_with_pin_code_card_switch)
        flashCardView = findViewById(R.id.flash_and_vibration_card_for_flash_wifi_state)
        vibrateCardView = findViewById(R.id.flash_and_vibration_card_for_vibration_wifi_state)
        flashSwitch = findViewById(R.id.flash_light_switch_wifi_state)
        vibrateSwitch = findViewById(R.id.vibration_switch_wifi_state)
        pressActivateButtonTextView = findViewById(R.id.press_activate_button_tv_wifi_state)
        activatingPleaseWaitTextView = findViewById(R.id.activating_please_wait_tv_wifi_state)
        activateTextView = findViewById(R.id.activate_tv_wifi_state)
        stopCircle = findViewById(R.id.wifi_state_detection_activation_stop_circle)
        stopTextView = findViewById(R.id.stop_tv_wifi_state)


        // Retrieve the switch state from SharedPreferences
        val sharedPreferencesSwitchState = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val switchState = sharedPreferencesSwitchState.getBoolean("switchState", false)

        flashSwitch.isChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY_FLASH, false)
        vibrateSwitch.isChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY_VIBRATION, false)


        flashSwitch.setOnCheckedChangeListener{ _, isChecked ->
            // Save the switch state in SharedPreferences
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY_FLASH, isChecked).apply()

            // Implement your logic when the switch is toggled (e.g., start or stop alarm)
            if (isChecked && isAnimationCompleted) {
                startService()

            } /*else {
            }*/
        }

        vibrateSwitch.setOnCheckedChangeListener{ _, isChecked ->
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

        /*redCircle = findViewById(R.id.red_circle_4)*/

        if(foregroundServiceRunning()){
            activateCircle.visibility = View.INVISIBLE
            stopCircle.visibility = View.VISIBLE
            activateTextView.visibility = View.INVISIBLE
            stopTextView.visibility = View.VISIBLE

            stopCircle.setOnClickListener{
                stopMediaService()
                navigateToMainActivity()
                mediaPlayer.stop()
            }

        }
        else {
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

        // Add a listener to detect when the animation completes
        animationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {}

            @SuppressLint("SetTextI18n")
            override fun onAnimationEnd(p0: Animator) {
                activateCircle.visibility = View.INVISIBLE
                isAnimationCompleted = true

            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}
        })
    }

    private fun startService() {
        if (!foregroundServiceRunning()) {
            // Retrieve the selected duration from SharedPreferences
            val selectedDuration = sharedPreferences.getInt(getString(R.string.key_selected_duration), -1)

            // Pass the volume setting and duration to the MediaService
            val serviceIntent = Intent(this, SensorsAndWifiService::class.java)
            serviceIntent.putExtra(SensorsAndWifiService.EXTRA_DURATION, selectedDuration)
            startForegroundService(serviceIntent)
        }
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


    override fun onDestroy() {
        super.onDestroy()
        // Unregister the broadcast receiver
        unregisterReceiver(wifiStateReceiver)
    }
}