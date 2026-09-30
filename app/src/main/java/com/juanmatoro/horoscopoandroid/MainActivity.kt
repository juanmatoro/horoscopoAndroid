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

    // Lista de datos estática con los 12 signos del zodíaco
    private val horoscopeList: List<Horoscope> = listOf(
        Horoscope("aries", R.string.horoscope_name_aries, R.string.horoscope_dates_aries, R.drawable.aries_icon),
        Horoscope("taurus", R.string.horoscope_name_taurus, R.string.horoscope_dates_taurus, R.drawable.taurus_icon),
        Horoscope("gemini", R.string.horoscope_name_gemini, R.string.horoscope_dates_gemini, R.drawable.gemini_icon),
        Horoscope("cancer", R.string.horoscope_name_cancer, R.string.horoscope_dates_cancer, R.drawable.cancer_icon),
        Horoscope("leo", R.string.horoscope_name_leo, R.string.horoscope_dates_leo, R.drawable.leo_icon),
        Horoscope("virgo", R.string.horoscope_name_virgo, R.string.horoscope_dates_virgo, R.drawable.virgo_icon),
        Horoscope("libra", R.string.horoscope_name_libra, R.string.horoscope_dates_libra, R.drawable.libra_icon),
        Horoscope("scorpio", R.string.horoscope_name_scorpio, R.string.horoscope_dates_scorpio, R.drawable.scorpio_icon),
        Horoscope("sagittarius", R.string.horoscope_name_sagittarius, R.string.horoscope_dates_sagittarius, R.drawable.sagittarius_icon),
        Horoscope("capricorn", R.string.horoscope_name_capricorn, R.string.horoscope_dates_capricorn, R.drawable.capricorn_icon),
        Horoscope("aquarius", R.string.horoscope_name_aquarius, R.string.horoscope_dates_aquarius, R.drawable.aquarius_icon),
        Horoscope("pisces", R.string.horoscope_name_pisces, R.string.horoscope_dates_pisces, R.drawable.pisces_icon),
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
