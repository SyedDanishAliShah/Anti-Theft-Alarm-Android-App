package com.example.antitheftalarmapp.adapterclasses

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.antitheftalarmapp.R
import com.example.antitheftalarmapp.dataclasses.IntruderPhoto

class CardAdapterIntruderPhotosList(
    private val context: Context,
    private val intrudersList: MutableList<IntruderPhoto>
) : RecyclerView.Adapter<CardAdapterIntruderPhotosList.ViewHolder>() {

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_intruders_photo_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val intruder = intrudersList[position]

        // Convert byte array to bitmap
        val bitmap = BitmapFactory.decodeByteArray(intruder.imageData, 0, intruder.imageData.size)

        // Rotate bitmap if necessary
        val rotatedBitmap = rotateBitmap(bitmap, intruder.rotationDegrees)

        // Bind data to views
        holder.personImage.setImageBitmap(rotatedBitmap)
        holder.deleteIcon.setOnClickListener {

            removeItemById(intruder)
        }
    }

    override fun getItemCount(): Int {
        return intrudersList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personImage: ImageView = itemView.findViewById(R.id.person_image)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
    }

    private fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun removeItemById(intruder: IntruderPhoto) {
        val position = intrudersList.indexOf(intruder)
        if (position != -1) {
            // Store the removed ID in SharedPreferences
            val removedIdSet = sharedPrefs.getStringSet("removedIds", mutableSetOf())
            removedIdSet?.add(intruder.uniqueId)
            sharedPrefs.edit().putStringSet("removedIds", removedIdSet).apply()

            intrudersList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, intrudersList.size)
        }
    }

}
