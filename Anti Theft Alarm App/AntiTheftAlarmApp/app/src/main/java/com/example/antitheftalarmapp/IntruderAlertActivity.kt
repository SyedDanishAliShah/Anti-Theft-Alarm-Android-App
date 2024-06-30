package com.example.antitheftalarmapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class IntruderAlertActivity : AppCompatActivity() {

    private lateinit var backIcon : ImageView
    private lateinit var intruderCard : ImageView
    private lateinit var takePhotoOnWrongPinIntruderSwitch : Switch
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "MyPrefs"
    private val SWITCH_STATE_KEY = "switchState"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intruder_alert)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        takePhotoOnWrongPinIntruderSwitch = findViewById(R.id.take_photo_intruder_switch)

        takePhotoOnWrongPinIntruderSwitch.isChecked = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)

        takePhotoOnWrongPinIntruderSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state in SharedPreferences
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isChecked).apply()

            // Implement your logic when the switch is toggled (e.g., start or stop alarm)
            if (isChecked) {

            } else {

            }
        }

        backIcon = findViewById(R.id.back_icon_intruder_alert)
        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        intruderCard = findViewById(R.id.intruder_photo_list_card)
        intruderCard.setOnClickListener {
            val intent = Intent(this, IntrudersPhotoListActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}