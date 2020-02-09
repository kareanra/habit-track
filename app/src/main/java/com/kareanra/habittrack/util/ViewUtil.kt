package com.kareanra.habittrack.util

import android.content.Context
import android.widget.Toast

fun Context?.showShortToast(text: String) =
    showToast(text, Toast.LENGTH_SHORT)

fun Context?.showLongToast(text: String) =
    showToast(text, Toast.LENGTH_LONG)

private fun Context?.showToast(text: String, duration: Int) =
    Toast.makeText(this, text, duration).show()
