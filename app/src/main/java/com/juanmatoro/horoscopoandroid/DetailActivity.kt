package com.juanmatoro.horoscopoandroid

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Pantalla de detalle que muestra la información completa del signo del horóscopo seleccionado.
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        // Clave constante utilizada para enviar y recibir el ID del signo mediante Intent
        const val EXTRA_HOROSCOPE_ID = "extra_horoscope_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Configuración para respetar las barras de estado y navegación del sistema (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Obtener el ID del signo enviado desde MainActivity mediante Intent extra
        val horoscopeId = intent.getStringExtra(EXTRA_HOROSCOPE_ID) ?: ""

        // 2. Buscar el objeto Horoscope correspondiente a través del proveedor de datos
        val horoscope = HoroscopeProvider.getHoroscopeById(horoscopeId)

        if (horoscope != null) {
            // 3. Obtener referencias visuales del layout activity_detail.xml
            val cardHeader: CardView = findViewById(R.id.cardHeader)
            val ivDetailIcon: ImageView = findViewById(R.id.ivDetailIcon)
            val tvDetailName: TextView = findViewById(R.id.tvDetailName)
            val tvDetailDates: TextView = findViewById(R.id.tvDetailDates)
            val tvDetailElement: TextView = findViewById(R.id.tvDetailElement)
            val tvDetailText: TextView = findViewById(R.id.tvDetailText)
            val btnBack: TextView = findViewById(R.id.btnBack)
            val btnLanguage: Button = findViewById(R.id.btnLanguage)

            // 4. Configurar el botón de cambio de idioma en la cabecera
            updateLanguageButtonVisuals(btnLanguage)
            btnLanguage.setOnClickListener {
                val currentLocales = AppCompatDelegate.getApplicationLocales()
                val currentLanguage = if (currentLocales.isEmpty) {
                    resources.configuration.locales[0]?.language ?: "en"
                } else {
                    currentLocales[0]?.language ?: "en"
                }
                val newLanguage = if (currentLanguage == "es") "en" else "es"
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLanguage))
            }

            // 5. Asignar los datos del signo seleccionado
            tvDetailName.text = getString(horoscope.name)
            tvDetailDates.text = getString(horoscope.dates)
            tvDetailElement.text = getString(horoscope.type.descriptionRes)
            tvDetailText.text = getString(horoscope.detail)
            ivDetailIcon.setImageResource(horoscope.icon)

            // Asignar el color del elemento a la tarjeta de cabecera
            val color = ContextCompat.getColor(this, horoscope.type.colorRes)
            cardHeader.setCardBackgroundColor(color)

            // Configurar el botón para regresar a la pantalla anterior
            btnBack.setOnClickListener {
                finish() // Cierra la Activity actual y regresa a la Activity previa
            }
        } else {
            finish()
        }
    }

    /**
     * Actualiza el aspecto visual del botón de idioma en la cabecera (texto con bandera y color de fondo)
     * indicando el idioma al que se cambiará al hacer clic (UX).
     *
     * @param btnLanguage Referencia al botón de cambio de idioma en la interfaz.
     */
    private fun updateLanguageButtonVisuals(btnLanguage: Button) {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentLanguage = if (currentLocales.isEmpty) {
            resources.configuration.locales[0]?.language ?: "en"
        } else {
            currentLocales[0]?.language ?: "en"
        }

        if (currentLanguage == "es") {
            // Si la app está en Español, el botón muestra "🇬🇧 EN" (invita a cambiar a Inglés)
            btnLanguage.setText(R.string.btn_language_en)
            val colorEn = ContextCompat.getColor(this, R.color.color_en)
            btnLanguage.backgroundTintList = ColorStateList.valueOf(colorEn)
            btnLanguage.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            // Si la app está en Inglés, el botón muestra "🇪🇸 ES" (invita a cambiar a Español)
            btnLanguage.setText(R.string.btn_language_es)
            val colorEs = ContextCompat.getColor(this, R.color.color_es)
            btnLanguage.backgroundTintList = ColorStateList.valueOf(colorEs)
            btnLanguage.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }
}
