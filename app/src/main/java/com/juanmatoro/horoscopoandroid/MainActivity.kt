package com.juanmatoro.horoscopoandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    val horoscopeList: List<Horoscope> = listOf(
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- BLOQUE DE PRUEBA DE DATOS DE HORÓSCOPO ---
        // Para probar en Logcat, simplemente descomenta este bucle:

        for (horoscope in horoscopeList) {
            val name = getString(horoscope.name)
            val dates = getString(horoscope.dates)
            Log.d("HOROSCOPO_TEST", "ID: ${horoscope.id} | Nombre: $name | Fechas: $dates | Icono Res ID: ${horoscope.icon}")
        }

    }
}