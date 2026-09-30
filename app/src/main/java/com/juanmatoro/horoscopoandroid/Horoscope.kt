package com.juanmatoro.horoscopoandroid

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Horoscope(
    val id: String,
    @param:StringRes val name: Int,
    @param:StringRes val dates: Int,
    @param:DrawableRes val icon: Int
)
