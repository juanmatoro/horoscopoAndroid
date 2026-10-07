package com.juanmatoro.horoscopoandroid.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utilidad centralizada para el formateo y gestión de fechas en la aplicación.
 */
object DateUtils {

    /**
     * Obtiene la fecha actual formateada en una cadena legible (ej. "05/10/2026").
     *
     * @param pattern Formato deseado para la fecha ("dd/MM/yyyy" por defecto).
     * @return Cadena de texto con la fecha actual formateada según la configuración regional.
     */
    fun getCurrentFormattedDate(pattern: String = "dd/MM/yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date())
    }
}
