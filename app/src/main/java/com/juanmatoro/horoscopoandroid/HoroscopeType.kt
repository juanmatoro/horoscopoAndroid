package com.juanmatoro.horoscopoandroid

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

/**
 * Enumeración que representa los 4 elementos de los signos del zodíaco.
 * Cada elemento tiene asociado su recurso de color y su nota/descripción genérica.
 *
 * @property colorRes Referencia al recurso de color definido en colors.xml
 * @property descriptionRes Referencia al recurso de texto con la nota/descripción genérica del elemento
 */
enum class HoroscopeType(
    @param:ColorRes val colorRes: Int,
    @param:StringRes val descriptionRes: Int
) {
    FIRE(R.color.color_fire, R.string.element_fire),     // Fuego: Tono cálido y nota descriptiva
    EARTH(R.color.color_earth, R.string.element_earth),  // Tierra: Tono verde y nota descriptiva
    AIR(R.color.color_air, R.string.element_air),        // Aire: Tono amarillo y nota descriptiva
    WATER(R.color.color_water, R.string.element_water)   // Agua: Tono azul y nota descriptiva
}
