package com.example.antitheftalarmapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.antitheftalarmapp.objectclasses.PinHolder

class PinCodeSettingActivity : AppCompatActivity() {

    private lateinit var currentPinCodeNumber: TextView
    private lateinit var currentPinCardView: ImageView
    private lateinit var showPasswordEye: ImageView
    private lateinit var hidePasswordEye: ImageView
    private lateinit var stopAlarmWithPinCodeCardView : ImageView
    private lateinit var stopAlarmWithPinCodeSwitch: Switch

    private var isPasswordVisible = false
    private var enteredPin: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "MyPrefs"
    private val SWITCH_STATE_KEY = "switchState"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code_settings)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        currentPinCodeNumber = findViewById(R.id.current_pin_code_tv)
        currentPinCardView = findViewById(R.id.current_pin_code_card)
        showPasswordEye = findViewById(R.id.show_hide_password_icon)
        hidePasswordEye = findViewById(R.id.hide_password_icon)
        stopAlarmWithPinCodeCardView = findViewById(R.id.stop_alarm_with_pin_code_card)
        stopAlarmWithPinCodeSwitch = findViewById(R.id.stop_alarm_with_pin_code_card_switch)

        stopAlarmWithPinCodeSwitch.isChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)

        stopAlarmWithPinCodeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state in SharedPreferences
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isChecked).apply()

            // Implement your logic when the switch is toggled (e.g., start or stop alarm)
            if (isChecked) {

            } else {

            }
        }


        if (!enteredPin.isNullOrBlank()) {
            currentPinCodeNumber.text = getString(R.string.hidden_pin) // Initial hidden state
            currentPinCardView.visibility = View.VISIBLE
        }

        showPasswordEye.setOnClickListener {
            showPassword()
        }

        hidePasswordEye.setOnClickListener {
            hidePassword()
        }

        val backIcon: ImageView = findViewById(R.id.back_icon_1)

        backIcon.setOnClickListener {
            // Get the PinHolder instance in your activity
            val pinHolder = PinHolder.getInstance(applicationContext)
            pinHolder.enteredPin = enteredPin.toString()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        val changePinCodeCardView: ImageView = findViewById(R.id.change_pin_code_card)

        changePinCodeCardView.setOnClickListener {
            val intent = Intent(this, EnterPinScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Get the PinHolder instance in your activity
        val pinHolder = PinHolder.getInstance(applicationContext)
        enteredPin = pinHolder.enteredPin
        // Update UI with the new enteredPin
        updateUIWithEnteredPin()
    }


    private fun updateUIWithEnteredPin() {
        // Update the UI with the entered pin, e.g., set it to your TextView
        currentPinCodeNumber.text = enteredPin
        // You may also update the visibility of your views based on the pin availability
    }

    private fun showPassword() {
        currentPinCodeNumber.text = enteredPin
        isPasswordVisible = true
        showPasswordEye.visibility = View.GONE
        hidePasswordEye.visibility = View.VISIBLE
    }

    private fun hidePassword() {
        currentPinCodeNumber.text = getString(R.string.hidden_pin)
        isPasswordVisible = false
        showPasswordEye.visibility = View.VISIBLE
        hidePasswordEye.visibility = View.GONE
    }
}