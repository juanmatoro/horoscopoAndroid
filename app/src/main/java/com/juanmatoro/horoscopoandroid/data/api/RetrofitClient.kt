package com.juanmatoro.horoscopoandroid.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Singleton que configura e inicializa la instancia de Retrofit
 * para comunicarse con el servidor de freehoroscopeapi.com.
 */
object RetrofitClient {

    // URL base del servidor de la API de horóscopos
    private const val BASE_URL = "https://freehoroscopeapi.com/"

    /**
     * Instancia única de [HoroscopeApiService] creada mediante la inicialización lazy de Kotlin.
     */
    val apiService: HoroscopeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HoroscopeApiService::class.java)
    }
}
