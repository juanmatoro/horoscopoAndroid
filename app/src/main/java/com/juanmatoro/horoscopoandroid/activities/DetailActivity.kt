package com.juanmatoro.horoscopoandroid.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanmatoro.horoscopoandroid.R
import com.juanmatoro.horoscopoandroid.data.FavoriteManager
import com.juanmatoro.horoscopoandroid.data.Horoscope
import com.juanmatoro.horoscopoandroid.data.HoroscopeProvider
import com.juanmatoro.horoscopoandroid.utils.showToast

/**
 * Pantalla de detalle que muestra la información completa del signo del horóscopo seleccionado,
 * permite marcar o desmarcar dicho signo como favorito (ícono de corazón) y compartir su contenido con otras aplicaciones.
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
     * Infla el menú superior (activity_detail_menu.xml) dentro de la Toolbar de detalle,
     * actualiza el icono del corazón (relleno en rojo si es favorito, silueta si no lo es)
     * y configura la opción del menú de idioma.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_detail_menu, menu)

        // 1. Actualiza el icono del corazón según si este signo es el favorito guardado del usuario
        val favoriteItem = menu.findItem(R.id.action_favorite)
        if (favoriteItem != null && currentHoroscopeId.isNotEmpty()) {
            val isFav = FavoriteManager.isFavorite(this, currentHoroscopeId)
            favoriteItem.setIcon(
                if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
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
     * Captura y procesa las opciones del menú seleccionadas por el usuario:
     * - Flecha Atrás: Regresa a MainActivity.
     * - Compartir: Inicia un Intent implícito (ACTION_SEND) para enviar la predicción a otras apps.
     * - Favorito: Marca o desmarca este signo como el favorito (cambia el corazón entre rojo y silueta).
     * - Idioma: Alterna dinámicamente el idioma de la aplicación (i18n).
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Flecha atrás: regresa a MainActivity
                finish()
                true
            }
            R.id.action_share -> {
                // Función Compartir: Comparte la predicción actual con otras aplicaciones
                val horoscope = HoroscopeProvider.getHoroscopeById(currentHoroscopeId)
                if (horoscope != null) {
                    shareHoroscope(horoscope)
                }
                true
            }
            R.id.action_favorite -> {
                // Alterna el estado de favorito del signo actual
                val isCurrentlyFavorite = FavoriteManager.isFavorite(this, currentHoroscopeId)
                val horoscope = HoroscopeProvider.getHoroscopeById(currentHoroscopeId)

                if (isCurrentlyFavorite) {
                    // Si ya era favorito, lo desmarca y elimina de SharedPreferences
                    FavoriteManager.clearFavorite(this)
                    item.setIcon(R.drawable.ic_heart_outline)
                    showToast(getString(R.string.favorite_removed_message))
                } else {
                    // Si no era favorito, lo guarda como favorito y muestra el corazón relleno en rojo
                    FavoriteManager.saveFavorite(this, currentHoroscopeId)
                    item.setIcon(R.drawable.ic_heart_filled)
                    val name = horoscope?.name?.let { getString(it) } ?: currentHoroscopeId
                    showToast("$name ${getString(R.string.favorite_set_success)}")
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

    /**
     * Prepara y dispara un Intent implícito de tipo ACTION_SEND con el selector (Chooser)
     * para compartir el texto traducido del signo del horóscopo con aplicaciones externas
     * (WhatsApp, Gmail, Mensajes, etc.).
     *
     * @param horoscope Objeto con la información del signo a compartir.
     */
    private fun shareHoroscope(horoscope: Horoscope) {
        val name = getString(horoscope.name)
        val dates = getString(horoscope.dates)
        val detail = getString(horoscope.detail)

        // Formateo del mensaje estructurado a compartir
        val shareText = "✨ $name ($dates) ✨\n\n$detail\n\n- ${getString(R.string.app_name)}"

        // Creación del Intent implícito ACTION_SEND para texto plano
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        // Creación del selector nativo de aplicaciones (Chooser)
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_title))
        startActivity(shareIntent)
    }
}
