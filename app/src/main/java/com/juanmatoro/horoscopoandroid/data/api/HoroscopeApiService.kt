package com.juanmatoro.horoscopoandroid.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que define las peticiones HTTP REST asíncronas a la API de freehoroscopeapi.com.
 *
 * USO DE CORRUTINAS DE KOTLIN:
 * Cada función está marcada con la palabra clave 'suspend' (función de suspensión).
 * Esto permite a Retrofit ejecutar la llamada de red en un hilo secundario (IO Thread)
 * de forma asíncrona y no bloqueante, suspendiendo la ejecución sin congelar la interfaz de usuario (UI Thread).
 */
interface HoroscopeApiService {

    /**
     * Petición HTTP GET asíncrona mediante Corrutina para consultar el horóscopo diario de un signo.
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
     * Petición HTTP GET asíncrona mediante Corrutina para consultar el horóscopo semanal de un signo.
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
     * Petición HTTP GET asíncrona mediante Corrutina para consultar el horóscopo mensual de un signo.
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
