package com.juanmatoro.horoscopoandroid.utils

import android.content.Context
import android.widget.Toast

/**
 * Función de extensión sobre [Context] para mostrar mensajes emergentes Toast de forma limpia y reutilizable.
 *
 * @param message Texto del mensaje a mostrar en pantalla.
 * @param duration Duración del Toast (Toast.LENGTH_SHORT por defecto).
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
