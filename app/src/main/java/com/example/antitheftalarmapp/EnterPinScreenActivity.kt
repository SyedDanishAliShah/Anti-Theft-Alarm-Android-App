package com.example.antitheftalarmapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.antitheftalarmapp.objectclasses.PinHolder

class EnterPinScreenActivity : AppCompatActivity(), View.OnClickListener {

    private var enteredPin = ""
    private var isPinEntered = false
    private var initialPin: String = ""
    private var isInitialAttempt = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin_code)

        val backIcon: ImageView = findViewById(R.id.back_icon_2)
        backIcon.setOnClickListener {
            val intent = Intent(this, PinCodeSettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        val numberTextViews: Array<ImageView> = arrayOf(
            findViewById(R.id.change_pin_code_numbers_circle_icon_1), findViewById(R.id.change_pin_code_numbers_circle_icon_2), findViewById(R.id.change_pin_code_numbers_circle_icon_3),
            findViewById(R.id.change_pin_code_numbers_circle_icon_4), findViewById(R.id.change_pin_code_numbers_circle_icon_5), findViewById(R.id.change_pin_code_numbers_circle_icon_6),
            findViewById(R.id.change_pin_code_numbers_circle_icon_7), findViewById(R.id.change_pin_code_numbers_circle_icon_8), findViewById(R.id.change_pin_code_numbers_circle_icon_9),
            findViewById(R.id.change_pin_code_numbers_circle_icon_0)
        )

        for (numberTextView in numberTextViews) {
            numberTextView.setOnClickListener(this)
        }

        val backSpace: ImageView = findViewById(R.id.change_pin_code_number_removal_icon_for_removal_icon)
        backSpace.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.substring(0, enteredPin.length - 1)
                updateEnterPinCircles(enteredPin.length)
            }
        }
    }

    override fun onClick(view: View) {
        if (view is ImageView) {
            enteredPin += view.tag.toString()
            updateEnterPinCircles(enteredPin.length)

            if (enteredPin.length == 4) {
                if (isInitialAttempt) {
                    initialPin = enteredPin

                    if (isAllCirclesFilled()) {
                        Handler().postDelayed({
                            val enterPinTextView = findViewById<TextView>(R.id.enter_passcode_tv)
                            enterPinTextView.visibility = View.INVISIBLE

                            val enterPinTextViewReenter = findViewById<TextView>(R.id.enter_passcode_tv_reenter)
                            enterPinTextViewReenter.visibility = View.VISIBLE

                            enteredPin = ""  // Clear enteredPin for re-entry
                            updateEnterPinCircles(0)
                            isInitialAttempt = false
                        }, 500)
                    } else {
                        updateEnterPinCircles(0)
                    }
                } else {
                    if (enteredPin == initialPin) {
                        // Get the PinHolder instance in your activity
                        val pinHolder = PinHolder.getInstance(applicationContext)
                        pinHolder.enteredPin = enteredPin
                        val intent = Intent(this, PinCodeSettingActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                    }
                }
            }
        }
    }

    private fun isAllCirclesFilled(): Boolean {
        val enterCircles: Array<ImageView> = arrayOf(
            findViewById(R.id.change_pin_code_circle_1),
            findViewById(R.id.change_pin_code_circle_2),
            findViewById(R.id.change_pin_code_circle_3),
            findViewById(R.id.change_pin_code_circle_4)
        )

        for (i in 0 until enteredPin.length) {
            enterCircles[i].setImageResource(R.drawable.change_password_filled_circle)
        }

        return enteredPin.length == enterCircles.size
    }

    private fun updateEnterPinCircles(enteredDigits: Int) {
        val enterCircles: Array<ImageView> = arrayOf(
            findViewById(R.id.change_pin_code_circle_1),
            findViewById(R.id.change_pin_code_circle_2),
            findViewById(R.id.change_pin_code_circle_3),
            findViewById(R.id.change_pin_code_circle_4)
        )

        for (i in enterCircles.indices) {
            if (i < enteredDigits) {
                enterCircles[i].setImageResource(R.drawable.change_password_filled_circle)
            } else {
                if (!isPinEntered) {
                    enterCircles[i].setImageResource(R.drawable.change_pin_code_circle)
                }
            }
        }
    }
}