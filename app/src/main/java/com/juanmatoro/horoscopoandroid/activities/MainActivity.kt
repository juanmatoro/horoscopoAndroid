package com.juanmatoro.horoscopoandroid.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.juanmatoro.horoscopoandroid.R
import com.juanmatoro.horoscopoandroid.adapters.HoroscopeAdapter
import com.juanmatoro.horoscopoandroid.data.FavoriteManager
import com.juanmatoro.horoscopoandroid.data.HoroscopeProvider
import com.juanmatoro.horoscopoandroid.utils.showToast

/**
 * Pantalla principal que muestra la lista de horóscopos mediante un RecyclerView,
 * permite buscar signos en tiempo real con logs explicativos y acceder al horóscopo favorito del usuario.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        // Etiqueta constante para identificar los mensajes de Logcat del buscador
        private const val SEARCH_TAG = "SEARCH_LOG"
    }

    // Referencia al adaptador del RecyclerView para actualizar la lista filtrada
    private lateinit var adapter: HoroscopeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configuración de la MaterialToolbar como ActionBar oficial de la pantalla
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configuración para respetar las barras de estado y navegación del sistema (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Obtener la referencia del RecyclerView definido en activity_main.xml
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // 2. Definir el LayoutManager (LinearLayoutManager muestra los elementos en lista vertical)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Crear e inicializar el Adapter con la lista completa y la acción de navegación al detalle
        adapter = HoroscopeAdapter(HoroscopeProvider.horoscopeList) { horoscope ->
            // Al hacer clic en un elemento, creamos un Intent explícito para abrir DetailActivity
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_HOROSCOPE_ID, horoscope.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    /**
     * Al volver a la pantalla principal (ej. desde DetailActivity), refresca el menú superior
     * para asegurar que el icono del corazón refleje dinámicamente si el usuario tiene o no un favorito guardado.
     */
    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu() // Fuerza la actualización de las opciones del menú superior
    }

    /**
     * Infla el menú superior (activity_main_menu.xml) en la Toolbar, configura
     * el icono del corazón (relleno en rojo si hay favorito, silueta si no lo hay),
     * los listeners con logs para el SearchView y el indicador de idioma activo (i18n).
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)

        // 1. Configuración del icono del corazón según si el usuario tiene un favorito guardado
        val favoriteItem = menu.findItem(R.id.action_favorite)
        if (favoriteItem != null) {
            val hasFavorite = FavoriteManager.getFavorite(this) != null
            favoriteItem.setIcon(
                if (hasFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
        }

        // 2. Configuración del SearchView interactivo e inserción de logs para monitorear pulsaciones
        val searchItem = menu.findItem(R.id.action_search)

        // Listener para detectar cuándo se despliega/expande o colapsa el buscador en la barra
        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                Log.d(SEARCH_TAG, "🔎 Buscador ABIERTO/EXPANDIDO por el usuario")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                Log.d(SEARCH_TAG, "❌ Buscador CERRADO/COLAPSADO por el usuario")
                return true
            }
        })

        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.search_hint)

        // Listener para capturar cada pulsación de tecla e ingreso de caracteres en el SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(SEARCH_TAG, "⌨️ Búsqueda confirmada (Enter/Submit): '$query'")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(SEARCH_TAG, "🔤 Carácter pulsado / Texto cambiado: '$newText'")
                filterHoroscopes(newText.orEmpty())
                return true
            }
        })

        // 3. Configura la opción de idioma mostrando la bandera/idioma al que se cambiará (UX)
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
     * Filtra la lista de horóscopos comparando el texto introducido en el SearchView
     * e imprime logs detallados del proceso de filtrado.
     *
     * @param query Texto de búsqueda ingresado por el usuario.
     */
    private fun filterHoroscopes(query: String) {
        val filteredList = HoroscopeProvider.horoscopeList.filter { horoscope ->
            val nameText = getString(horoscope.name)
            nameText.contains(query, ignoreCase = true) || horoscope.id.contains(query, ignoreCase = true)
        }

        // Log que detalla el texto buscado y cuántos resultados coinciden
        Log.d(SEARCH_TAG, "🎯 Filtrando por término: '$query' -> Resultados encontrados: ${filteredList.size}")
        adapter.updateList(filteredList)
    }

    /**
     * Captura y responde a los eventos de pulsación sobre las opciones del menú de la Toolbar.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                // Función Favorito: Obtiene el ID del signo favorito guardado por el usuario
                val favoriteId = FavoriteManager.getFavorite(this)

                if (favoriteId != null) {
                    val intent = Intent(this, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_HOROSCOPE_ID, favoriteId)
                    }
                    startActivity(intent)
                } else {
                    showToast(getString(R.string.favorite_none_saved), Toast.LENGTH_LONG)
                }
                true
            }
            R.id.action_language -> {
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
