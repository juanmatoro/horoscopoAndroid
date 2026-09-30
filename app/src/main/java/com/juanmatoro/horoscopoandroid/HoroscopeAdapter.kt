package com.juanmatoro.horoscopoandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvDates: TextView = view.findViewById(R.id.tvDates)

        /**
         * Asigna los datos de un [Horoscope] a las vistas correspondientes.
         */
        fun render(horoscope: Horoscope) {
            val context = itemView.context
            // Obtiene los textos localizados usando los IDs de recursos
            tvName.text = context.getString(horoscope.name)
            tvDates.text = context.getString(horoscope.dates)
            // Asigna el ícono desde el recurso drawable
            ivIcon.setImageResource(horoscope.icon)
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
