package com.juanmatoro.horoscopoandroid.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que define las peticiones HTTP REST a la API de freehoroscopeapi.com.
 */
interface HoroscopeApiService {

    /**
     * Petición HTTP GET para consultar el horóscopo diario de un signo del zodíaco.
     *
     * @param sign ID/Nombre del signo en inglés (ej. "aries", "taurus", "virgo").
     * @return [Response] envolviendo la respuesta [HoroscopeResponse].
     */
    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/daily")
    suspend fun getDailyHoroscope(
        @Query("sign") sign: String
    ): Response<HoroscopeResponse>

    /**
     * Petición HTTP GET para consultar el horóscopo semanal de un signo del zodíaco.
     *
     * @param sign ID/Nombre del signo en inglés (ej. "aries", "taurus", "virgo").
     * @return [Response] envolviendo la respuesta [HoroscopeResponse].
     */
    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/weekly")
    suspend fun getWeeklyHoroscope(
        @Query("sign") sign: String
    ): Response<HoroscopeResponse>

    /**
     * Petición HTTP GET para consultar el horóscopo mensual de un signo del zodíaco.
     *
     * @param sign ID/Nombre del signo en inglés (ej. "aries", "taurus", "virgo").
     * @return [Response] envolviendo la respuesta [HoroscopeResponse].
     */
    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/monthly")
    suspend fun getMonthlyHoroscope(
        @Query("sign") sign: String
    ): Response<HoroscopeResponse>
}
