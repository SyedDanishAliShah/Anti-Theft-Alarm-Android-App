package com.example.antitheftalarmapp.adapterclasses



import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.antitheftalarmapp.R

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val verticalSpacing: Int,
    private val includeEdge: Boolean,
    private val fixedMarginStartEven: Int,
    private val fixedMarginStartOdd: Int,
    private val fixedMarginBottom: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {

        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        // Calculate total vertical spacing
        val totalVerticalSpacing = verticalSpacing * (spanCount + 1) / spanCount

        val itemTopSpacing = verticalSpacing // Set a fixed value for top spacing
        val itemBottomSpacing = verticalSpacing

        // Set left and right spacing for all columns
        outRect.left = verticalSpacing
        outRect.right = verticalSpacing

        // Set top and bottom spacing for all rows
        outRect.top = itemTopSpacing
        outRect.bottom = itemBottomSpacing

        outRect.left = if (position % 2 == 0) fixedMarginStartEven else fixedMarginStartOdd
        outRect.bottom = fixedMarginBottom

        // Adjust left and right spacing for the first and last columns
        if (includeEdge) {
            outRect.left = if (column == 0) totalVerticalSpacing else itemTopSpacing
            outRect.right = if (column == spanCount - 1) totalVerticalSpacing else itemTopSpacing
        }

        // Apply item orientation logic
        val layoutParams = view.layoutParams as RecyclerView.LayoutParams

        if (position == 4) {
            //val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
           // val titleLayoutParams = titleTextView.layoutParams as ViewGroup.MarginLayoutParams
            //titleLayoutParams.bottomMargin = fixedMarginBottom
            //titleTextView.layoutParams = titleLayoutParams

            view.findViewById<ImageView>(R.id.cardView)

        }else if (position == 7) {
            //val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            //val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
            val description = "  FacebookWhatsapp                    Share"
            // Find the start and end indices of the "Facebook" text
            val facebookStart = description.indexOf("Facebook")
            val facebookEnd = facebookStart + "Facebook".length

            // Add spaces to the end of the "Facebook" text to create margin
            val marginSpaces = "                "  // Adjust the number of spaces as needed
            val spacedFacebook = "Facebook$marginSpaces"

            // Create a StringBuilder with the spaced version
            val spacedDescription = StringBuilder(description)
            spacedDescription.replace(facebookStart, facebookEnd, spacedFacebook)

            // Apply the spaced version to your TextView
            //descriptionTextView.text = spacedDescription.toString()
            //val descriptionLayoutParams = descriptionTextView.layoutParams as ViewGroup.MarginLayoutParams
            //descriptionLayoutParams.bottomMargin = 65
            //descriptionLayoutParams.marginEnd = 25
            //descriptionTextView.layoutParams = descriptionLayoutParams
            //val titleLayoutParams = titleTextView.layoutParams as ViewGroup.MarginLayoutParams
            //titleLayoutParams.bottomMargin = 180
            //titleTextView.layoutParams = titleLayoutParams
        }
        else if (position % 2 == 0 && position != 4){
            //val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            //val titleTextViewLayoutParams = titleTextView.layoutParams as ViewGroup.MarginLayoutParams
            //titleTextViewLayoutParams.leftMargin = fixedMarginStartEven
            //titleTextView.layoutParams = titleTextViewLayoutParams
        }
        else {
           // val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            //val titleLayoutParams = titleTextView.layoutParams as ViewGroup.MarginLayoutParams
            //titleLayoutParams.leftMargin = fixedMarginStartOdd
            //titleTextView.layoutParams = titleLayoutParams
        }

        view.layoutParams = layoutParams
    }
}




