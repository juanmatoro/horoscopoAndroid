package com.juanmatoro.horoscopoandroid.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

/**
 * Gestor centralizado para la traducción On-Device (en el dispositivo) utilizando Google ML Kit.
 *
 * USO DE CORRUTINAS DE KOTLIN:
 * Utiliza la extensión '.await()' del paquete 'kotlinx.coroutines.tasks.await' para convertir
 * las tareas asíncronas basadas en 'Task<T>' de Google Play Services en funciones de suspensión
 * compatibles con Corrutinas de Kotlin ('suspend fun').
 */
object TranslationManager {

    /**
     * Función de suspensión ('suspend fun') que traduce un texto en inglés al español de forma local.
     * Al ser 'suspend', se debe invocar dentro de un Coroutine Scope (ej. 'lifecycleScope.launch').
     *
     * @param text Texto en inglés a traducir.
     * @return El texto traducido al español o el texto original en inglés si ocurre una excepción.
     */
    suspend fun translateEnToEs(text: String): String {
        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.SPANISH)
                .build()

            val translator = Translation.getClient(options)

            // CORRUTINAS: '.await()' suspende la ejecución hasta que el modelo de lenguaje de Google se descarga en segundo plano
            translator.downloadModelIfNeeded().await()

            // CORRUTINAS: '.await()' suspende la ejecución hasta que finaliza la traducción On-Device
            translator.translate(text).await()
        } catch (e: Exception) {
            text // En caso de fallo o primera descarga en curso, retorna el texto original de respaldo
        }
    }
}
