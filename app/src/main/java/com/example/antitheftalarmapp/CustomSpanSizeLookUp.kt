package com.example.antitheftalarmapp

import androidx.recyclerview.widget.GridLayoutManager

class CustomSpanSizeLookup(private val totalSpanCount: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {

        return if (position == 4 || position == 7) totalSpanCount else 1
    }
}