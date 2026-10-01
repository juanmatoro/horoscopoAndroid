package com.juanmatoro.horoscopoandroid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Pantalla principal que muestra la lista de horóscopos mediante un RecyclerView.
 */
class MainActivity : AppCompatActivity() {

    // Lista de datos estática con los 12 signos del zodíaco y su tipo de elemento (Fuego, Tierra, Aire, Agua)
    private val horoscopeList: List<Horoscope> = listOf(
        Horoscope("aries", R.string.horoscope_name_aries, R.string.horoscope_dates_aries, R.drawable.aries_icon, HoroscopeType.FIRE),
        Horoscope("taurus", R.string.horoscope_name_taurus, R.string.horoscope_dates_taurus, R.drawable.taurus_icon, HoroscopeType.EARTH),
        Horoscope("gemini", R.string.horoscope_name_gemini, R.string.horoscope_dates_gemini, R.drawable.gemini_icon, HoroscopeType.AIR),
        Horoscope("cancer", R.string.horoscope_name_cancer, R.string.horoscope_dates_cancer, R.drawable.cancer_icon, HoroscopeType.WATER),
        Horoscope("leo", R.string.horoscope_name_leo, R.string.horoscope_dates_leo, R.drawable.leo_icon, HoroscopeType.FIRE),
        Horoscope("virgo", R.string.horoscope_name_virgo, R.string.horoscope_dates_virgo, R.drawable.virgo_icon, HoroscopeType.EARTH),
        Horoscope("libra", R.string.horoscope_name_libra, R.string.horoscope_dates_libra, R.drawable.libra_icon, HoroscopeType.AIR),
        Horoscope("scorpio", R.string.horoscope_name_scorpio, R.string.horoscope_dates_scorpio, R.drawable.scorpio_icon, HoroscopeType.WATER),
        Horoscope("sagittarius", R.string.horoscope_name_sagittarius, R.string.horoscope_dates_sagittarius, R.drawable.sagittarius_icon, HoroscopeType.FIRE),
        Horoscope("capricorn", R.string.horoscope_name_capricorn, R.string.horoscope_dates_capricorn, R.drawable.capricorn_icon, HoroscopeType.EARTH),
        Horoscope("aquarius", R.string.horoscope_name_aquarius, R.string.horoscope_dates_aquarius, R.drawable.aquarius_icon, HoroscopeType.AIR),
        Horoscope("pisces", R.string.horoscope_name_pisces, R.string.horoscope_dates_pisces, R.drawable.pisces_icon, HoroscopeType.WATER)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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

        // 3. Conectar el Adapter pasándole la lista de horóscopos
        recyclerView.adapter = HoroscopeAdapter(horoscopeList)
    }
}
