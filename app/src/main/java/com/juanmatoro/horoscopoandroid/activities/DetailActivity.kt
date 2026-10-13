package com.juanmatoro.horoscopoandroid.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.juanmatoro.horoscopoandroid.R
import com.juanmatoro.horoscopoandroid.data.FavoriteManager
import com.juanmatoro.horoscopoandroid.data.Horoscope
import com.juanmatoro.horoscopoandroid.data.HoroscopeProvider
import com.juanmatoro.horoscopoandroid.data.api.HoroscopePeriod
import com.juanmatoro.horoscopoandroid.data.api.RetrofitClient
import com.juanmatoro.horoscopoandroid.utils.TranslationManager
import com.juanmatoro.horoscopoandroid.utils.showToast
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle que muestra la información completa del signo del horóscopo seleccionado,
 * permite cambiar entre predicción Diaria, Semanal o Mensual mediante el menú inferior (BottomNavigationView),
 * consulta la API REST freehoroscopeapi.com en tiempo real,
 * traduce automáticamente el texto al español en el dispositivo mediante Google ML Kit,
 * permite marcar el signo como favorito y compartir su contenido con otras aplicaciones.
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        // Clave constante utilizada para enviar y recibir el ID del signo mediante Intent
        const val EXTRA_HOROSCOPE_ID = "extra_horoscope_id"
    }

    // Guardamos la referencia del ID del signo actual en esta pantalla
    private var currentHoroscopeId: String = ""

    // Referencias a los componentes de la interfaz
    private lateinit var ivDetailFavoriteBadge: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvApiStatus: TextView
    private lateinit var tvDetailText: TextView
    private lateinit var bottomNavigationViewPeriod: BottomNavigationView

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
            ivDetailFavoriteBadge = findViewById(R.id.ivDetailFavoriteBadge)
            val tvDetailName: TextView = findViewById(R.id.tvDetailName)
            val tvDetailDates: TextView = findViewById(R.id.tvDetailDates)
            val tvDetailElement: TextView = findViewById(R.id.tvDetailElement)
            tvDetailText = findViewById(R.id.tvDetailText)
            progressBar = findViewById(R.id.progressBar)
            tvApiStatus = findViewById(R.id.tvApiStatus)
            bottomNavigationViewPeriod = findViewById(R.id.bottomNavigationViewPeriod)

            // Asignar el nombre del signo como título de la Toolbar
            supportActionBar?.title = getString(horoscope.name)

            // 4. Asignar inicialmente los datos locales predeterminados
            tvDetailName.text = getString(horoscope.name)
            tvDetailDates.text = getString(horoscope.dates)
            tvDetailElement.text = getString(horoscope.type.descriptionRes)
            tvDetailText.text = getString(horoscope.detail)
            ivDetailIcon.setImageResource(horoscope.icon)

            // Actualizar el estado visual del corazón pequeño en la esquina de la imagen
            updateFavoriteBadge()

            // Asignar el color del elemento a la tarjeta de cabecera
            val color = ContextCompat.getColor(this, horoscope.type.colorRes)
            cardHeader.setCardBackgroundColor(color)

            // Configuración del listener para alternar entre el periodo Diario, Semanal o Mensual desde el BottomNavigationView
            bottomNavigationViewPeriod.setOnItemSelectedListener { item ->
                val selectedPeriod = when (item.itemId) {
                    R.id.nav_period_weekly -> HoroscopePeriod.WEEKLY
                    R.id.nav_period_monthly -> HoroscopePeriod.MONTHLY
                    else -> HoroscopePeriod.DAILY
                }
                fetchHoroscopeFromApi(horoscope, selectedPeriod)
                true
            }

            // 5. Consultar la predicción inicial (Diaria) desde la API REST y traducir On-Device
            fetchHoroscopeFromApi(horoscope, HoroscopePeriod.DAILY)
        } else {
            finish()
        }
    }

    /**
     * Realiza una llamada asíncrona a la API REST para el periodo seleccionado (Diario, Semanal, Mensual) mediante Corrutinas.
     *
     * EXPLICACIÓN DE CORRUTINAS:
     * 1. 'lifecycleScope.launch': Inicia una corrutina en el hilo principal (Main Thread), pero vinculada al ciclo
     *    de vida de DetailActivity. Si el usuario abandona la pantalla, la corrutina se cancela automáticamente.
     * 2. 'RetrofitClient.apiService.get*Horoscope()': Al ser funciones 'suspend', Retrofit traslada automáticamente la
     *    petición de red a un hilo de E/S secundario (Dispatchers.IO) sin bloquear la interfaz visual.
     * 3. 'TranslationManager.translateEnToEs()': Función 'suspend' que ejecuta la traducción local asíncrona con ML Kit.
     *
     * @param horoscope Objeto Horoscope del signo actual.
     * @param period Periodo a consultar ([HoroscopePeriod.DAILY], [HoroscopePeriod.WEEKLY], [HoroscopePeriod.MONTHLY]).
     */
    private fun fetchHoroscopeFromApi(horoscope: Horoscope, period: HoroscopePeriod = HoroscopePeriod.DAILY) {
        // Muestra el indicador de carga ProgressBar en la interfaz
        progressBar.visibility = View.VISIBLE
        tvApiStatus.visibility = View.GONE

        // INICIO DE CORRUTINA: 'lifecycleScope.launch' crea un ámbito asíncrono seguro
        lifecycleScope.launch {
            try {
                // SUSPENSIÓN #1: Petición de red asíncrona no bloqueante
                val response = when (period) {
                    HoroscopePeriod.DAILY -> RetrofitClient.apiService.getDailyHoroscope(horoscope.id)
                    HoroscopePeriod.WEEKLY -> RetrofitClient.apiService.getWeeklyHoroscope(horoscope.id)
                    HoroscopePeriod.MONTHLY -> RetrofitClient.apiService.getMonthlyHoroscope(horoscope.id)
                }

                if (response.isSuccessful && response.body()?.data != null) {
                    val apiData = response.body()!!.data!!
                    val apiDate = apiData.date.orEmpty()
                    val rawPrediction = apiData.horoscope.orEmpty()

                    if (rawPrediction.isNotEmpty()) {
                        // Detectar el idioma actual de la aplicación (i18n)
                        val currentLocales = AppCompatDelegate.getApplicationLocales()
                        val currentLanguage = if (currentLocales.isEmpty) {
                            resources.configuration.locales[0]?.language ?: "en"
                        } else {
                            currentLocales[0]?.language ?: "en"
                        }

                        // SUSPENSIÓN #2: Traducción asíncrona On-Device con Google ML Kit
                        val finalPrediction = if (currentLanguage == "es") {
                            TranslationManager.translateEnToEs(rawPrediction)
                        } else {
                            rawPrediction
                        }

                        // Retorno al hilo principal: Se actualizan las vistas de la UI con los resultados
                        tvDetailText.text = finalPrediction
                        tvApiStatus.text = "🟢 ${getString(R.string.status_api_updated)} ($apiDate)"
                    } else {
                        tvApiStatus.text = "🟡 ${getString(R.string.status_api_outdated)}"
                    }
                } else {
                    tvApiStatus.text = "⚪ ${getString(R.string.status_api_offline)}"
                }
            } catch (e: Exception) {
                tvApiStatus.text = "⚪ ${getString(R.string.status_api_offline)}"
            } finally {
                // Oculta el ProgressBar de carga y muestra la etiqueta de estado
                progressBar.visibility = View.GONE
                tvApiStatus.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Muestra u oculta el corazón pequeño superpuesto en la esquina superior derecha del ícono
     * según si este signo es el favorito activo del usuario.
     */
    private fun updateFavoriteBadge() {
        if (::ivDetailFavoriteBadge.isInitialized && currentHoroscopeId.isNotEmpty()) {
            val isFav = FavoriteManager.isFavorite(this, currentHoroscopeId)
            ivDetailFavoriteBadge.visibility = if (isFav) View.VISIBLE else View.GONE
        }
    }

    /**
     * Infla el menú superior (activity_detail_menu.xml) dentro de la Toolbar de detalle,
     * actualiza el icono del corazón en la barra y configura la opción del menú de idioma.
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
     * - Favorito: Marca o desmarca este signo como el favorito del usuario.
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
                    updateFavoriteBadge()
                    showToast(getString(R.string.favorite_removed_message))
                } else {
                    // Si no era favorito, lo guarda como favorito en SharedPreferences
                    FavoriteManager.saveFavorite(this, currentHoroscopeId)
                    item.setIcon(R.drawable.ic_heart_filled)
                    updateFavoriteBadge()
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
        val detail = tvDetailText.text.toString()

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
