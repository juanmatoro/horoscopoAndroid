package com.juanmatoro.horoscopoandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Adaptador para el RecyclerView que conecta la lista de objetos [Horoscope]
 * con las vistas definidas en [R.layout.item_horoscope].
 */
class HoroscopeAdapter(
    private val horoscopeList: List<Horoscope>
) : RecyclerView.Adapter<HoroscopeAdapter.HoroscopeViewHolder>() {

    /**
     * ViewHolder que mantiene las referencias a los componentes visuales
     * de un solo elemento (item) de la lista.
     */
    class HoroscopeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Referencia al contenedor CardView para cambiar el color de fondo
        private val cardContainer: CardView = view.findViewById(R.id.cardContainer)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvDates: TextView = view.findViewById(R.id.tvDates)
        private val tvDescription: TextView = view.findViewById(R.id.tvDescription)

        /**
         * Asigna los datos de un [Horoscope] a las vistas correspondientes.
         *
         * NOTA DE INTERNACIONALIZACIÓN (i18n / Localización Novedosa de Android):
         * Al llamar a `context.getString(R.string.*)`, el sistema de Android selecciona
         * automáticamente el texto localizado desde el archivo 'strings.xml' correspondiente
         * al idioma configurado en el dispositivo del usuario (ej. values-es/strings.xml para español).
         */
        fun render(horoscope: Horoscope) {
            val context = itemView.context

            // Obtención de textos traducidos automáticamente según el idioma del dispositivo
            tvName.text = context.getString(horoscope.name)
            tvDates.text = context.getString(horoscope.dates)
            tvDescription.text = context.getString(horoscope.type.descriptionRes)

            // Asignación del ícono desde recursos gráficos
            ivIcon.setImageResource(horoscope.icon)

            // Asignación del color del elemento (Fuego, Tierra, Aire, Agua)
            val color = ContextCompat.getColor(context, horoscope.type.colorRes)
            cardContainer.setCardBackgroundColor(color)
        }
    }

    /**
     * Infla (crea) el diseño XML del item (item_horoscope.xml)
     * y crea una nueva instancia del ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horoscope, parent, false)
        return HoroscopeViewHolder(view)
    }

    /**
     * Une los datos del elemento en la posición [position] con el [holder] correspondiente.
     */
    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        holder.render(horoscopeList[position])
    }

    /**
     * Retorna la cantidad total de elementos en la lista.
     */
    override fun getItemCount(): Int = horoscopeList.size
}
