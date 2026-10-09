package com.juanmatoro.horoscopoandroid.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

/**
 * Gestor centralizado para la traducción On-Device (en el dispositivo) utilizando Google ML Kit.
 */
object TranslationManager {

    /**
     * Traduce un texto en inglés al español de forma local en el dispositivo.
     * Descarga el modelo de lenguaje de Google la primera vez si es necesario.
     *
     * @param text Texto en inglés a traducir.
     * @return El texto traducido al español o el texto original si no se pudo traducir.
     */
    suspend fun translateEnToEs(text: String): String {
        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build()

            val translator = Translation.getClient(options)

            // Descarga automática del paquete de idioma en el dispositivo si es la primera vez
            translator.downloadModelIfNeeded().await()

            // Ejecución de la traducción rápida On-Device
            translator.translate(text).await()
        } catch (e: Exception) {
            text
        }
    }
}
