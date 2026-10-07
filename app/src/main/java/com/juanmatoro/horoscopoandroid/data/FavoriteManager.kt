package com.juanmatoro.horoscopoandroid.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de almacenamiento mediante SharedPreferences para guardar y consultar
 * de forma persistente el signo del horóscopo seleccionado como favorito por el usuario.
 */
object FavoriteManager {

    // Nombre del archivo de preferencias privadas de la aplicación
    private const val PREFS_NAME = "horoscope_preferences"

    // Clave para almacenar el ID del signo favorito (ej. "aries", "taurus")
    private const val KEY_FAVORITE_ID = "key_favorite_horoscope_id"

    /**
     * Obtiene la instancia de SharedPreferences de la aplicación.
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guarda el ID del signo del horóscopo seleccionado como favorito.
     *
     * @param context Contexto de la aplicación o Activity.
     * @param horoscopeId ID único en texto del signo (ej. "virgo").
     */
    fun saveFavorite(context: Context, horoscopeId: String) {
        getPreferences(context)
            .edit()
            .putString(KEY_FAVORITE_ID, horoscopeId)
            .apply()
    }

    /**
     * Obtiene el ID del signo favorito guardado por el usuario.
     *
     * @param context Contexto de la aplicación o Activity.
     * @return El ID del signo favorito guardado o null si aún no se ha seleccionado ninguno.
     */
    fun getFavorite(context: Context): String? {
        return getPreferences(context).getString(KEY_FAVORITE_ID, null)
    }

    /**
     * Comprueba si un signo específico es actualmente el favorito guardado.
     *
     * @param context Contexto de la aplicación o Activity.
     * @param horoscopeId ID del signo a comprobar.
     * @return true si es el favorito activo, false en caso contrario.
     */
    fun isFavorite(context: Context, horoscopeId: String): Boolean {
        return getFavorite(context) == horoscopeId
    }

    /**
     * Elimina la selección del horóscopo favorito guardado.
     *
     * @param context Contexto de la aplicación o Activity.
     */
    fun clearFavorite(context: Context) {
        getPreferences(context)
            .edit()
            .remove(KEY_FAVORITE_ID)
            .apply()
    }
}
