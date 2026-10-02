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
 *
 * @param horoscopeList Lista de elementos [Horoscope] a mostrar.
 * @param onHoroscopeSelected Función lambda invocada al hacer clic sobre una celda.
 */
class HoroscopeAdapter(
    private val horoscopeList: List<Horoscope>,
    private val onHoroscopeSelected: (Horoscope) -> Unit
) : RecyclerView.Adapter<HoroscopeAdapter.HoroscopeViewHolder>() {

    /**
     * ViewHolder que mantiene las referencias a los componentes visuales
     * de un solo elemento (item) de la lista.
     */
    class HoroscopeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Referencia al contenedor CardView para cambiar el color de fondo y detectar pulsaciones
        private val cardContainer: CardView = view.findViewById(R.id.cardContainer)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvDates: TextView = view.findViewById(R.id.tvDates)
        private val tvDescription: TextView = view.findViewById(R.id.tvDescription)

        /**
         * Asigna los datos de un [Horoscope] a las vistas correspondientes
         * y configura la captura del evento de pulsación/clic sobre la celda.
         */
        fun render(horoscope: Horoscope, onHoroscopeSelected: (Horoscope) -> Unit) {
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

            // Detección de la pulsación sobre la tarjeta para navegar al detalle
            cardContainer.setOnClickListener {
                onHoroscopeSelected(horoscope)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horoscope, parent, false)
        return HoroscopeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        holder.render(horoscopeList[position], onHoroscopeSelected)
    }

    override fun getItemCount(): Int = horoscopeList.size
}
