package com.example.antitheftalarmapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.antitheftalarmapp.adapterclasses.CardAdapterIntruderPhotosList
import com.example.antitheftalarmapp.dataclasses.IntruderPhoto
import java.util.UUID

class IntrudersPhotoListActivity : AppCompatActivity() {

    private lateinit var backIconIntruder: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPrefs: SharedPreferences
    private val PREF_NAME = "MyPrefs"


    // Declare intrudersPhotosList and cardAdapter as member variables
    private val intrudersPhotosList = mutableListOf<IntruderPhoto>()
    private lateinit var cardAdapter: CardAdapterIntruderPhotosList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intruders_photo_list)

        sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


        backIconIntruder = findViewById(R.id.back_icon_intruder_alert_description)
        backIconIntruder.setOnClickListener {
            val intent = Intent(this, IntruderAlertActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView_1)

        // Initialize cardAdapter
        cardAdapter = CardAdapterIntruderPhotosList(this, intrudersPhotosList)
        recyclerView.adapter = cardAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
    }

    override fun onResume() {
        super.onResume()

        val removedIdSet = sharedPrefs.getStringSet("removedIds", mutableSetOf())

        // Remove items that are in removedIds
        intrudersPhotosList.removeAll { removedIdSet?.contains(it.uniqueId) ?: true }

        val imageString = sharedPrefs.getString("lastCapturedImage", null)
        if (!imageString.isNullOrEmpty()) {
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            val photoId = UUID.randomUUID().toString() // Generate a unique ID

            val existingPhoto = intrudersPhotosList.find { it.uniqueId == photoId }
            if (existingPhoto == null) {
                val intruderPhoto = IntruderPhoto(
                    uniqueId = photoId,
                    id = System.currentTimeMillis(), // Use a different ID here if needed
                    imageData = imageBytes,
                    title = "Title",
                    description = "Description",
                    rotationDegrees = 270.0f
                )
                intrudersPhotosList.add(intruderPhoto)
                cardAdapter.notifyDataSetChanged()
            }
        }
    }

}