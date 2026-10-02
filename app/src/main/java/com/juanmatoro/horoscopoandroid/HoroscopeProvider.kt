package com.juanmatoro.horoscopoandroid

/**
 * Proveedor de datos centralizado para acceder a la lista de horóscopos y buscar por ID.
 */
object HoroscopeProvider {

    /**
     * Lista estática con la información de los 12 signos del zodíaco.
     */
    val horoscopeList: List<Horoscope> = listOf(
        Horoscope("aries", R.string.horoscope_name_aries, R.string.horoscope_dates_aries, R.drawable.aries_icon, HoroscopeType.FIRE, R.string.horoscope_detail_aries),
        Horoscope("taurus", R.string.horoscope_name_taurus, R.string.horoscope_dates_taurus, R.drawable.taurus_icon, HoroscopeType.EARTH, R.string.horoscope_detail_taurus),
        Horoscope("gemini", R.string.horoscope_name_gemini, R.string.horoscope_dates_gemini, R.drawable.gemini_icon, HoroscopeType.AIR, R.string.horoscope_detail_gemini),
        Horoscope("cancer", R.string.horoscope_name_cancer, R.string.horoscope_dates_cancer, R.drawable.cancer_icon, HoroscopeType.WATER, R.string.horoscope_detail_cancer),
        Horoscope("leo", R.string.horoscope_name_leo, R.string.horoscope_dates_leo, R.drawable.leo_icon, HoroscopeType.FIRE, R.string.horoscope_detail_leo),
        Horoscope("virgo", R.string.horoscope_name_virgo, R.string.horoscope_dates_virgo, R.drawable.virgo_icon, HoroscopeType.EARTH, R.string.horoscope_detail_virgo),
        Horoscope("libra", R.string.horoscope_name_libra, R.string.horoscope_dates_libra, R.drawable.libra_icon, HoroscopeType.AIR, R.string.horoscope_detail_libra),
        Horoscope("scorpio", R.string.horoscope_name_scorpio, R.string.horoscope_dates_scorpio, R.drawable.scorpio_icon, HoroscopeType.WATER, R.string.horoscope_detail_scorpio),
        Horoscope("sagittarius", R.string.horoscope_name_sagittarius, R.string.horoscope_dates_sagittarius, R.drawable.sagittarius_icon, HoroscopeType.FIRE, R.string.horoscope_detail_sagittarius),
        Horoscope("capricorn", R.string.horoscope_name_capricorn, R.string.horoscope_dates_capricorn, R.drawable.capricorn_icon, HoroscopeType.EARTH, R.string.horoscope_detail_capricorn),
        Horoscope("aquarius", R.string.horoscope_name_aquarius, R.string.horoscope_dates_aquarius, R.drawable.aquarius_icon, HoroscopeType.AIR, R.string.horoscope_detail_aquarius),
        Horoscope("pisces", R.string.horoscope_name_pisces, R.string.horoscope_dates_pisces, R.drawable.pisces_icon, HoroscopeType.WATER, R.string.horoscope_detail_pisces)
    )

    /**
     * Busca y retorna un [Horoscope] a partir de su ID único.
     *
     * @param id Identificador en texto del signo (ej. "aries", "taurus").
     * @return El objeto [Horoscope] encontrado o null si no existe.
     */
    fun getHoroscopeById(id: String): Horoscope? {
        return horoscopeList.find { it.id == id }
    }
}
