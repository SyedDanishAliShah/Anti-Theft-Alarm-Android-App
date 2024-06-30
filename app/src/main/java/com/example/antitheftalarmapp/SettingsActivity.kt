package com.example.antitheftalarmapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.antitheftalarmapp.Services.SensorsAndWifiService

class SettingsActivity : AppCompatActivity() {


    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private var currentSoundResourceId: Int = -1
    private val SHARED_PREFS_FILE_NAME = "MySharedPrefs"
    private var handler: Handler? = null
    private var delayDuration: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)

        // Initialize the SeekBar in your Activity or Fragment:
        val seekBar = findViewById<View>(R.id.seekBar) as SeekBar

        // Get the audio manager
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        // Set the maximum volume of the SeekBar to the maximum volume of the MediaPlayer:
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekBar.max = maxVolume

        // Set the current volume of the SeekBar to the current volume of the MediaPlayer:
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        seekBar.progress = currVolume


        // Find the back_icon ImageView
        val backIcon: ImageView = findViewById(R.id.back_icon)

        // Set OnClickListener for the back_icon ImageView
        backIcon.setOnClickListener {
            // Navigate to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity to prevent it from being on the back stack
        }


        val selectAlarmToneCardView : ImageView = findViewById(R.id.alarm_tone_card)

        selectAlarmToneCardView.setOnClickListener {
            showAlarmToneDialog()
        }

        val alarmDurationCardView : ImageView = findViewById(R.id.alarm_duration_card)

        alarmDurationCardView.setOnClickListener {
            showAlarmDurationDialog()
        }

        val pinCodeSettingCardView : ImageView = findViewById(R.id.pin_code_settings_card)

        pinCodeSettingCardView.setOnClickListener {
            val intent = Intent(this, PinCodeSettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Add a SeekBar.OnSeekBarChangeListener to the SeekBar:
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.FLAG_SHOW_UI)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do Nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do Nothing
            }
        })
    }


    private fun showAlarmToneDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alarm_tone_alert_dialog)

        // Set the desired width and height for the dialog
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = resources.getDimensionPixelSize(R.dimen.custom_dialog_width)
        layoutParams.height = resources.getDimensionPixelSize(R.dimen.custom_dialog_height)
        dialog.window?.attributes = layoutParams

        val toneARadioButton: RadioButton = dialog.findViewById(R.id.tone_a_radio_button)
        toneARadioButton.tag = R.raw.club_alarma

        val toneBRadioButton : RadioButton = dialog.findViewById(R.id.tone_b_radio_button)
        toneBRadioButton.tag = R.raw.free_music_in_my_mind_remake_26367

        val toneCRadioButton : RadioButton = dialog.findViewById(R.id.tone_c_radio_button)
        toneCRadioButton.tag = R.raw.lion_dance_101_sound_boards

        val toneDRadioButton : RadioButton = dialog.findViewById(R.id.tone_d_radio_button)
        toneDRadioButton.tag = R.raw.nokia_5130_rck_101_sound_boards

        val toneERadioButton : RadioButton = dialog.findViewById(R.id.tone_e_radio_button)
        toneERadioButton.tag = R.raw.nokia_5800_101_sound_boards

        val toneFRadioButton : RadioButton = dialog.findViewById(R.id.tone_f_radio_button)
        toneFRadioButton.tag = R.raw.sinister_156638

        val toneGRadioButton : RadioButton = dialog.findViewById(R.id.tone_g_radio_button)
        toneGRadioButton.tag = R.raw.suspect_156639

        // Set click listeners for radio buttons
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.tone_radio_group)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = dialog.findViewById<RadioButton>(checkedId)
            currentSoundResourceId = radioButton.tag as Int
            playSound(currentSoundResourceId)
        }

        // Find the TextView for settings
        val selectTextView: TextView = dialog.findViewById(R.id.select)

        // Inside your selectTextView.setOnClickListener block
        selectTextView.setOnClickListener {
            // Save the selected sound to SharedPreferences
            saveSelectedSound(radioGroup)

            stopCurrentSound()

            // Dismiss the dialog when the select TextView is clicked
            dialog.dismiss()

        }
        // Load the saved sound for each radio button
        loadSavedSound(radioGroup)

        // Show the custom dialog
        dialog.show()
    }

    private fun stopCurrentSound() {
        // Stop the currently playing sound
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }


    private fun showAlarmDurationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alarm_duration_alert_dialog)

        // Set the desired width and height for the dialog
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = resources.getDimensionPixelSize(R.dimen.custom_dialog_width)
        layoutParams.height = resources.getDimensionPixelSize(R.dimen.custom_dialog_height)
        dialog.window?.attributes = layoutParams

        val fiveSecRadioButton: RadioButton = dialog.findViewById(R.id.five_sec_radio_button)
        fiveSecRadioButton.tag = FIVE_SECONDS

        val tenSecRadioButton : RadioButton = dialog.findViewById(R.id.ten_sec_radio_button)
        tenSecRadioButton.tag = TEN_SECONDS

        val fifteenSecRadioButton : RadioButton = dialog.findViewById(R.id.fifteen_sec_radio_button)
        fifteenSecRadioButton.tag = FIFTEEN_SECONDS

        val twentySecRadioButton : RadioButton = dialog.findViewById(R.id.twenty_sec_radio_button)
        twentySecRadioButton.tag = TWENTY_SECONDS

        val twentyFiveSecRadioButton : RadioButton = dialog.findViewById(R.id.twenty_five_sec_radio_button)
        twentyFiveSecRadioButton.tag = TWENTY_FIVE_SECONDS

        val thirtySecRadioButton : RadioButton = dialog.findViewById(R.id.thirty_sec_radio_button)
        thirtySecRadioButton.tag = THIRTY_SECONDS

        // Set click listeners for radio buttons
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.duration_radio_group)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = dialog.findViewById<RadioButton>(checkedId)
            delayDuration = radioButton.tag as Int
        }

        // Find the TextView for settings
        val selectTextView: TextView = dialog.findViewById(R.id.select_1)

        // Set OnClickListener for the select TextView
        selectTextView.setOnClickListener {
            // Save the selected duration to SharedPreferences
            saveSelectedDuration(delayDuration)
            // Dismiss the dialog when the select TextView is clicked

            dialog.dismiss()

            // Stop the MediaService after the selected duration
            stopMediaServiceDelayed(delayDuration * 1000L) // Convert seconds to milliseconds

        }

        // Load the saved duration for each radio button
        loadSavedDuration(radioGroup)

        // Show the custom dialog
        dialog.show()
    }

    private fun saveSelectedDuration(duration: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(getString(R.string.key_selected_duration), duration)
        editor.apply()
    }

    private fun stopMediaServiceDelayed(delayMillis: Long) {
        // Initialize the handler if it's null
        if (handler == null) {
            handler = Handler()
        }

        // Post a delayed runnable to stop the service
        handler?.postDelayed({
            // Stop the MediaService
            val serviceIntent = Intent(this, SensorsAndWifiService::class.java)
            stopService(serviceIntent)
        }, delayMillis)
    }

    private fun loadSavedDuration(radioGroup: RadioGroup) {
        val selectedDuration = sharedPreferences.getInt(getString(R.string.key_selected_duration), -1)

        if (selectedDuration != -1) {
            // Set the saved duration for the selected radio button
            for (i in 0 until radioGroup.childCount) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                if (radioButton.tag as Int == selectedDuration) {
                    radioButton.isChecked = true
                    break
                }
            }
        }
    }

    private fun saveSelectedSound(radioGroup: RadioGroup) {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        if (checkedRadioButtonId != -1) {
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(checkedRadioButtonId)
            val selectedSoundResourceId = selectedRadioButton.tag as Int

            // Save the selected sound to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putInt(getString(R.string.key_selected_sound), selectedSoundResourceId)
            editor.apply()

        }
    }

    private fun loadSavedSound(radioGroup: RadioGroup) {
        val selectedSoundResourceId = sharedPreferences.getInt(
            getString(R.string.key_selected_sound),
            -1
        )

        if (selectedSoundResourceId != -1) {
            // Set the saved sound for the selected radio button
            for (i in 0 until radioGroup.childCount) {
                val radioButton = radioGroup.getChildAt(i) as RadioButton
                if (radioButton.tag as Int == selectedSoundResourceId) {
                    radioButton.isChecked = true
                    break
                }
            }
        }
    }

    private fun playSound(soundResourceId: Int) {
        // Stop any currently playing sound
        mediaPlayer.stop()
        mediaPlayer.reset()

        // Set the new sound
        mediaPlayer = MediaPlayer.create(this, soundResourceId)

        // Start playing the sound
        mediaPlayer.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        // Unbind from the MediaService
        mediaPlayer.release()
    }

    companion object{
        private const val FIVE_SECONDS = 5
        private const val TEN_SECONDS = 10
        private const val FIFTEEN_SECONDS = 15
        private const val TWENTY_SECONDS = 20
        private const val TWENTY_FIVE_SECONDS = 25
        private const val THIRTY_SECONDS = 30
    }



}
