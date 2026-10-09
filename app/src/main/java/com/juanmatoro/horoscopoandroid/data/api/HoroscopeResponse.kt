package com.juanmatoro.horoscopoandroid.data.api

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que mapea la respuesta JSON principal enviada por la API de freehoroscopeapi.com.
 *
 * @property data Objeto interno que contiene los detalles de la predicción.
 */
data class HoroscopeResponse(
    @SerializedName("data") val data: HoroscopeData?
)

/**
 * Modelo interno con los campos de la predicción retornados por el servidor.
 *
 * @property date Fecha de la predicción entregada por el servidor en formato ISO ("yyyy-MM-dd", ej. "2026-10-09").
 * @property period Periodo de consulta de la predicción ("daily").
 * @property sign Nombre del signo retornado por la API.
 * @property horoscope Texto detallado de la predicción astrológica en vivo.
 */
data class HoroscopeData(
    @SerializedName("date") val date: String?,
    @SerializedName("period") val period: String?,
    @SerializedName("sign") val sign: String?,
    @SerializedName("horoscope") val horoscope: String?
)
