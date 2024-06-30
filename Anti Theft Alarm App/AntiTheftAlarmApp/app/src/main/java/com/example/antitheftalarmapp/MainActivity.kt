package com.example.antitheftalarmapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.antitheftalarmapp.adapterclasses.CardAdapter
import com.example.antitheftalarmapp.dataclasses.CardData
import com.google.android.material.navigation.NavigationView
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find other views from the main layout (activity_main.xml)
        drawerLayout = findViewById(R.id.myDrawerLayout)
        val menuIcon: ImageView = findViewById(R.id.menu_icon)
        val navigationView: NavigationView = findViewById(R.id.navigation_view_drawer)
        val headerView: View = navigationView.getHeaderView(0)
        val rateUsTextView: TextView = headerView.findViewById(R.id.rate_us_1)
        val shareTextView: TextView = headerView.findViewById(R.id.share_2)
        val settingsIcon: ImageView = findViewById(R.id.settings_icon)
        recyclerView = findViewById(R.id.recyclerView)


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val cardDataList = mutableListOf(
            CardData("Intruder Alert", "Take selfie when someone enters wrong password"),
            CardData("Motion Detect", "Alarm"),
            CardData("Anti Pocket Detect Alarm", "Alarm when remove from Pocket"),
            CardData("Charger Detect Alarm", "Alarm when charger is unplugged"),
            CardData("Advertisement", ""),
            CardData("Wifi State Change Detect Alarm", "Alarm when someone try to on/off wifi"),
            CardData("Full Battery Detect Alarm", "Alarm when Battery fully charged"),
          //  CardData("Share with Friends", "Facebook        Whatsapp                    Share")
            // Add more data as needed*/
        )

        val cardAdapter = CardAdapter(cardDataList)
        recyclerView.adapter = cardAdapter

       // recyclerView.addItemDecoration(GridSpacingItemDecoration(1, 0, false, 9, 8, 48))

        val gridLayoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)

        val customSpanSizeLookup = CustomSpanSizeLookup(1)
        gridLayoutManager.spanSizeLookup = customSpanSizeLookup

        recyclerView.layoutManager = gridLayoutManager


        // init action bar drawer toggle
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        // add a drawer listener into drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // show menu icon and back icon while drawer open
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set click listener for the menu_icon ImageView
        menuIcon.setOnClickListener {
            // Toggle the visibility of the drawer
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        settingsIcon.setOnClickListener {
            // Start the SettingsActivity when the settings icon is clicked
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        shareTextView.setOnClickListener {
            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Your share message")
            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, "No suitable app found", Toast.LENGTH_SHORT).show()
            }
        }

        rateUsTextView.setOnClickListener {
            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)

            val mainLayoutRate = findViewById<DrawerLayout>(R.id.myDrawerLayout)

            // Set alpha to make the RelativeLayout faded
            val relativeLayoutRate =
                mainLayoutRate.findViewById<RelativeLayout>(R.id.myRelativeLayout)
            relativeLayoutRate.alpha = 0.3f  // You can adjust the alpha value as needed

            // Make views inside the RelativeLayout unclickable
            for (i in 0 until relativeLayoutRate.childCount) {
                relativeLayoutRate.getChildAt(i).isClickable = false
            }

            // Inflate the centered_cardview.xml layout
            val inflater = LayoutInflater.from(this)
            val centeredCardViewLayout = inflater.inflate(R.layout.centered_card_view, mainLayoutRate, false)

            val notNowButton = centeredCardViewLayout.findViewById<ImageView>(R.id.rate_us_card_button)

            // Find the CardView in the inflated layout
            val centeredCardView = centeredCardViewLayout.findViewById<ImageView>(R.id.splash_screen_ad_rectangle_1)

            // Add a transparent overlay covering the entire screen
            val overlay = View(this)
            overlay.layoutParams = DrawerLayout.LayoutParams(
                DrawerLayout.LayoutParams.MATCH_PARENT,
                DrawerLayout.LayoutParams.MATCH_PARENT
            )
            overlay.setBackgroundColor(Color.TRANSPARENT)
            mainLayoutRate.addView(overlay)

            // Set an OnClickListener to the overlay to make the card view disappear when clicked outside of it
            overlay.setOnClickListener {
                // Remove the inflated layout and overlay
                mainLayoutRate.removeView(centeredCardViewLayout)
                mainLayoutRate.removeView(overlay)

                // Reset alpha to make the RelativeLayout visible
                relativeLayoutRate.alpha = 1.0f

                // Make views inside the RelativeLayout clickable again
                for (i in 0 until relativeLayoutRate.childCount) {
                    relativeLayoutRate.getChildAt(i).isClickable = true
                }
            }

            // Add the inflated layout to the main layout
            mainLayoutRate.addView(centeredCardViewLayout)

            // Make the CardView visible
            centeredCardView.visibility = View.VISIBLE

            // Find the "Not Now" button inside the CardView

           //  notNowButton: ImageView = centeredCardView.findViewById(R.id.rate_us_card_button)


            // Set an OnClickListener for the "Not Now" button
            notNowButton.setOnClickListener {
                // Remove the inflated layout and overlay
                mainLayoutRate.removeView(centeredCardViewLayout)
                mainLayoutRate.removeView(overlay)

                // Reset alpha to make the RelativeLayout visible
                relativeLayoutRate.alpha = 1.0f

                // Make views inside the RelativeLayout clickable again
                for (i in 0 until relativeLayoutRate.childCount) {
                    relativeLayoutRate.getChildAt(i).isClickable = true
                }
            }

            val starIcons = listOf(
                centeredCardViewLayout.findViewById<ImageView>(R.id.rate_us_drawer_icon),
                centeredCardViewLayout.findViewById(R.id.rate_us_drawer_icon_1),
                centeredCardViewLayout.findViewById(R.id.rate_us_drawer_icon_2),
                centeredCardViewLayout.findViewById(R.id.rate_us_drawer_icon_3),
                centeredCardViewLayout.findViewById(R.id.rate_us_drawer_icon_4)
            )

            var clickedIndex = -1 // Initialize to an invalid index

            for ((index, starIcon) in starIcons.withIndex()) {
                starIcon.setOnClickListener {
                    // Update the clickedIndex
                    clickedIndex = index

                    // Apply the flood fill algorithm to change the color
                    floodFill(starIcons.subList(0, clickedIndex + 1), Color.YELLOW)
                }
            }


// Set a single click listener for the entire card view
            centeredCardView.setOnClickListener {
                // After a delay of 500 milliseconds (half a second), close the CardView
                Handler().postDelayed({
                    centeredCardView.visibility = View.GONE

                    // Reset alpha to make the RelativeLayout visible
                    relativeLayoutRate.alpha = 1.0f

                    // Make views inside the RelativeLayout clickable again
                    for (i in 0 until relativeLayoutRate.childCount) {
                        relativeLayoutRate.getChildAt(i).isClickable = true
                    }

                    // Remove the overlay
                    mainLayoutRate.removeView(overlay)
                }, 500)
            }

            // Set an OnClickListener for the CardView to prevent it from being removed when clicked
            centeredCardView.setOnClickListener {
                // Do nothing or add any specific actions you want to perform when the card view is clicked
            }
        }
    }

    private fun floodFill(starIcons: List<ImageView>, targetColor: Int) {
        for (starIcon in starIcons) {
            val bitmap =
                (starIcon.drawable as? BitmapDrawable)?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
            bitmap?.let {
                val width = it.width
                val height = it.height

                val pixels = IntArray(width * height)
                it.getPixels(pixels, 0, width, 0, 0, width, height)

                val queue = LinkedList<Pair<Int, Int>>()
                val initialColor = pixels[width / 2 + height / 2 * width]

                if (initialColor != targetColor) {
                    queue.add(Pair(width / 2, height / 2))

                    while (queue.isNotEmpty()) {
                        val (x, y) = queue.poll()

                        if (x in 0 until width && y >= 0 && y < height && pixels[x + y * width] == initialColor) {
                            pixels[x + y * width] = targetColor
                            queue.add(Pair(x - 1, y))
                            queue.add(Pair(x + 1, y))
                            queue.add(Pair(x, y - 1))
                            queue.add(Pair(x, y + 1))
                        }
                    }

                    it.setPixels(pixels, 0, width, 0, 0, width, height)
                    starIcon.setImageBitmap(it)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // check condition for drawer item with menu item
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}






