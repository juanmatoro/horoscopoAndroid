package com.juanmatoro.horoscopoandroid

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Pantalla de detalle que muestra la información completa del signo del horóscopo seleccionado,
 * y permite marcar o desmarcar dicho signo como el favorito del usuario.
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        // Clave constante utilizada para enviar y recibir el ID del signo mediante Intent
        const val EXTRA_HOROSCOPE_ID = "extra_horoscope_id"
    }

    // Guardamos la referencia del ID del signo actual en esta pantalla
    private var currentHoroscopeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Configurar la MaterialToolbar como ActionBar con botón de navegación hacia atrás
        val toolbar: Toolbar = findViewById(R.id.toolbarDetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configuración para respetar las barras de estado y navegación del sistema (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Obtener el ID del signo enviado desde MainActivity mediante Intent extra
        currentHoroscopeId = intent.getStringExtra(EXTRA_HOROSCOPE_ID) ?: ""

        // 2. Buscar el objeto Horoscope correspondiente a través del proveedor de datos
        val horoscope = HoroscopeProvider.getHoroscopeById(currentHoroscopeId)

        if (horoscope != null) {
            // 3. Obtener referencias visuales del layout activity_detail.xml
            val cardHeader: CardView = findViewById(R.id.cardHeader)
            val ivDetailIcon: ImageView = findViewById(R.id.ivDetailIcon)
            val tvDetailName: TextView = findViewById(R.id.tvDetailName)
            val tvDetailDates: TextView = findViewById(R.id.tvDetailDates)
            val tvDetailElement: TextView = findViewById(R.id.tvDetailElement)
            val tvDetailText: TextView = findViewById(R.id.tvDetailText)

            // Asignar el nombre del signo como título de la Toolbar
            supportActionBar?.title = getString(horoscope.name)

            // 4. Asignar los datos del signo seleccionado
            tvDetailName.text = getString(horoscope.name)
            tvDetailDates.text = getString(horoscope.dates)
            tvDetailElement.text = getString(horoscope.type.descriptionRes)
            tvDetailText.text = getString(horoscope.detail)
            ivDetailIcon.setImageResource(horoscope.icon)

            // Asignar el color del elemento a la tarjeta de cabecera
            val color = ContextCompat.getColor(this, horoscope.type.colorRes)
            cardHeader.setCardBackgroundColor(color)
        } else {
            finish()
        }
    }

    /**
     * Infla el menú superior (activity_detail_menu.xml) dentro de la Toolbar de detalle
     * y actualiza el icono de la estrella según si este signo es el favorito guardado.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_detail_menu, menu)

        // 1. Actualiza el icono de la estrella si este signo ya es el favorito del usuario
        val favoriteItem = menu.findItem(R.id.action_favorite)
        if (favoriteItem != null && currentHoroscopeId.isNotEmpty()) {
            val isFav = FavoriteManager.isFavorite(this, currentHoroscopeId)
            favoriteItem.setIcon(
                if (isFav) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
            )
        }

        // 2. Actualiza la opción de idioma
        val languageItem = menu.findItem(R.id.action_language)
        if (languageItem != null) {
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            val currentLanguage = if (currentLocales.isEmpty) {
                resources.configuration.locales[0]?.language ?: "en"
            } else {
                currentLocales[0]?.language ?: "en"
            }

            if (currentLanguage == "es") {
                languageItem.setTitle(R.string.btn_language_en)
            } else {
                languageItem.setTitle(R.string.btn_language_es)
            }
        }
        return true
    }

    /**
     * Captura las opciones seleccionadas en el menú, permitiendo marcar/desmarcar
     * este signo como favorito en SharedPreferences.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Flecha atrás: regresa a MainActivity
                finish()
                true
            }
            R.id.action_favorite -> {
                // Alterna el estado de favorito del signo actual
                val isCurrentlyFavorite = FavoriteManager.isFavorite(this, currentHoroscopeId)
                val horoscope = HoroscopeProvider.getHoroscopeById(currentHoroscopeId)

                if (isCurrentlyFavorite) {
                    // Si ya era favorito, lo desmarca y elimina de SharedPreferences
                    FavoriteManager.clearFavorite(this)
                    item.setIcon(android.R.drawable.btn_star_big_off)
                    Toast.makeText(this, getString(R.string.favorite_removed_message), Toast.LENGTH_SHORT).show()
                } else {
                    // Si no era favorito, lo guarda como favorito en SharedPreferences
                    FavoriteManager.saveFavorite(this, currentHoroscopeId)
                    item.setIcon(android.R.drawable.btn_star_big_on)
                    val name = horoscope?.name?.let { getString(it) } ?: currentHoroscopeId
                    Toast.makeText(this, "$name ${getString(R.string.favorite_set_success)}", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.action_language -> {
                // Alterna el idioma de la app usando AppCompatDelegate
                val currentLocales = AppCompatDelegate.getApplicationLocales()
                val currentLanguage = if (currentLocales.isEmpty) {
                    resources.configuration.locales[0]?.language ?: "en"
                } else {
                    currentLocales[0]?.language ?: "en"
                }
                val newLanguage = if (currentLanguage == "es") "en" else "es"
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLanguage))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
