package com.simplemobiletools.notes.pro.extensions

import android.widget.Toast
import com.simplemobiletools.notes.pro.App

fun showToast(message: String) {
    Toast.makeText(App.application, message, Toast.LENGTH_SHORT).show()
}