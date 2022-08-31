package com.example.challenge4.extension

import android.view.MenuItem
import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun MenuItem.hide() {
    isVisible = false
}

fun MenuItem.show() {
    isVisible = true
}
