package com.juanmatoro.horoscopoandroid

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Modelo de datos que representa un signo del zodíaco.
 *
 * @property id Identificador único en formato texto (ej. "aries", "taurus").
 * @property name Referencia al recurso de texto con el nombre del signo (R.string.horoscope_name_*).
 * @property dates Referencia al recurso de texto con el rango de fechas (R.string.horoscope_dates_*).
 * @property icon Referencia al recurso ejecutable del ícono (R.drawable.*_icon).
 * @property type Elemento del signo (Fuego, Tierra, Aire, Agua) que determina su color.
 */
data class Horoscope(
    val id: String,
    @param:StringRes val name: Int,
    @param:StringRes val dates: Int,
    @param:DrawableRes val icon: Int,
    val type: HoroscopeType
)
