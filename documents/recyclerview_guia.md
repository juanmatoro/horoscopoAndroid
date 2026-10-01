# Guía de RecyclerView en Android

Un **RecyclerView** es el componente oficial de Android para mostrar colecciones de datos (listas o grillas) de manera eficiente.

Su nombre viene de **"Reciclar Vistas"**: en lugar de crear una vista nueva para cada elemento de la lista (lo cual consumiría mucha memoria si hay 1000 elementos), el `RecyclerView` solo crea las vistas necesarias para llenar la pantalla visible y las **reutiliza** a medida que el usuario hace scroll.

---

## Los 3 Pilares Principales

Para implementar un `RecyclerView` necesitamos tres piezas clave:

| Componente | Rol | Análogo en la vida real |
| :--- | :--- | :--- |
| **Activity / Fragment** | La pantalla contenedora. Prepara el `RecyclerView` y decide la orientación (lista vertical, horizontal, grilla). | El marco o la pared donde colgarás los cuadros. |
| **Adapter (Adaptador)** | El intermediario que conecta los **datos** con la **interfaz**. Toma cada objeto de la lista y se lo entrega al `ViewHolder`. | El camarero que toma la comida de la cocina y la lleva a la mesa. |
| **ViewHolder** | Mantiene ("sostiene") las referencias a los componentes visuales (`TextView`, `ImageView`) de una sola celda para no buscarlos repetidamente. | La bandeja con los platos listos para servir. |

---

## Componentes con Ejemplos del Código

### 1. El Modelo de Datos (`Horoscope.kt`)

Es la clase que representa la información de un solo elemento de la lista.

```kotlin
data class Horoscope(
    val id: String,
    @param:StringRes val name: Int,
    @param:StringRes val dates: Int,
    @param:DrawableRes val icon: Int,
    val type: HoroscopeType
)
```

---

### 2. El Diseño de la Celda (`item_horoscope.xml`)

Es el archivo XML que define cómo se verá **un solo elemento** en la lista.

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="16dp">

    <ImageView
        android:id="@+id/ivIcon"
        android:layout_width="56dp"
        android:layout_height="56dp" />

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <TextView
            android:id="@+id/tvDates"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
    </LinearLayout>
</LinearLayout>
```

---

### 3. El Adaptador y ViewHolder (`HoroscopeAdapter.kt`)

El adaptador extiende de `RecyclerView.Adapter` e implementa **3 métodos obligatorios**:

```kotlin
class HoroscopeAdapter(
    private val horoscopeList: List<Horoscope>
) : RecyclerView.Adapter<HoroscopeAdapter.HoroscopeViewHolder>() {

    // 1. VIEWHOLDER: Guarda las referencias visuales de la celda
    class HoroscopeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvDates: TextView = view.findViewById(R.id.tvDates)

        // Asigna los datos a los controles visuales
        fun render(horoscope: Horoscope) {
            val context = itemView.context
            tvName.text = context.getString(horoscope.name)
            tvDates.text = context.getString(horoscope.dates)
            ivIcon.setImageResource(horoscope.icon)
        }
    }

    // 2. CREAR VIEWHOLDER: Infla el XML (item_horoscope.xml) cuando se necesita una celda nueva
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horoscope, parent, false)
        return HoroscopeViewHolder(view)
    }

    // 3. VINCULAR DATOS: Asigna los datos de la posición actual a la celda
    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        val item = horoscopeList[position]
        holder.render(item)
    }

    // 4. CANTIDAD DE ELEMENTOS: Indica cuántos elementos hay en la lista
    override fun getItemCount(): Int = horoscopeList.size
}
```

#### ¿Qué hace cada método del Adapter?

- **`onCreateViewHolder`**: Se ejecuta **solo unas cuantas veces** (al inicio o al hacer scroll si se necesitan más celdas). Se encarga de cargar (inflar) el diseño XML (`item_horoscope.xml`) y crear el `ViewHolder`.
- **`onBindViewHolder`**: Se ejecuta **constantemente** al hacer scroll. Recibe una celda ya creada (el `ViewHolder`) y la posición del elemento en la lista, y le pasa los datos correspondientes.
- **`getItemCount`**: Le dice al `RecyclerView` cuántas celdas en total tiene que dibujar.

---

### 4. La Activity (`MainActivity.kt`)

En la Activity se conectan todas las piezas en 3 pasos:

```kotlin
class MainActivity : AppCompatActivity() {

    private val horoscopeList: List<Horoscope> = listOf( /* ... tus datos ... */ )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // PASO 1: Obtener la vista del RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // PASO 2: Asignar un LayoutManager (define cómo se posicionan los ítems: lista vertical, grilla, etc.)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // PASO 3: Crear el Adapter con los datos y asignárselo al RecyclerView
        recyclerView.adapter = HoroscopeAdapter(horoscopeList)
    }
}
```

---

## Flujo de Funcionamiento

```text
[ Lista de Datos (List<Horoscope>) ]
               │
               ▼
       [ HoroscopeAdapter ]
         ├── onCreateViewHolder() ──► Infla item_horoscope.xml y crea el ViewHolder
         ├── onBindViewHolder()   ──► Asigna los datos de la posición X al ViewHolder
         └── getItemCount()       ──► Retorna el número de elementos
               │
               ▼
[ RecyclerView dibujado en MainActivity ]
```

---

## Solución a Bugs Comunes de Scroll

### Bug: El último elemento queda cortado o pegado a la barra de navegación del sistema

**Causa**: Por defecto, un `RecyclerView` recorta las celdas exactamente dentro de su área visible (`clipToPadding = true`). Cuando la pantalla incluye barras de sistema o bordes, el último elemento toca el borde inferior sin dejar margen de separación.

**Solución**:
1. Agrega `android:clipToPadding="false"` en el XML del `RecyclerView`. Esto permite que los elementos se puedan desplazar a través del área de relleno.
2. Agrega espacio al final de la lista con `android:paddingBottom="16dp"`.

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:clipToPadding="false"
    android:paddingBottom="16dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/tvTitle"
    tools:listitem="@layout/item_horoscope" />
```
