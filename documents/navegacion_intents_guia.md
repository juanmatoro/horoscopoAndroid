# Guía de Navegación e Intents en Android (Paso a Paso)

En Android, para cambiar de una pantalla (**Activity**) a otra y pasar información entre ellas, utilizamos **Intents Explícitos**.

---

## Conceptos Clave

1. **Activity**: Representa cada pantalla independiente de la aplicación.
2. **Intent Explícito**: Un mensaje que le indica al sistema operativo Android: *"Quiero abrir específicamente la pantalla Y partiendo desde la pantalla X"*.
3. **Extras**: Datos adicionales que adjuntamos dentro del `Intent` (como una mochila con clave-valor) para enviárselos a la pantalla destino.
4. **`finish()`**: Método que cierra la pantalla actual y regresa a la pantalla anterior del historial (Back Stack).

---

## Paso Previo de Prueba: Uso de `Toast` durante el Desarrollo

Durante el desarrollo, **antes de crear la nueva pantalla (`DetailActivity`)**, es una excelente práctica verificar que la pulsación sobre la celda del `RecyclerView` funciona correctamente mostrando un mensaje emergente temporal llamado **`Toast`**.

### ¿Qué es un `Toast`?

Un `Toast` es una pequeña notificación flotante que aparece brevemente en la parte inferior de la pantalla para dar retroalimentación rápida al usuario o al desarrollador.

Sintaxis básica:
```kotlin
Toast.makeText(contexto, "Texto del mensaje", Toast.LENGTH_SHORT).show()
```

### ¿Cómo nos ayuda durante el desarrollo?

Nos ayuda a validar **3 aspectos fundamentales** antes de construir la pantalla de detalle:
1. **Detección del toque**: Confirma que el listener `setOnClickListener` de la celda responde al clic.
2. **Comunicación del Adaptador**: Verifica que el evento se transmite correctamente desde el `HoroscopeAdapter` hasta la `MainActivity`.
3. **Integridad de los datos**: Asegura que el objeto `Horoscope` seleccionado es el correcto al hacer clic (ej. al pulsar la primera celda recibimos los datos de Aries).

### Ejemplo de código de prueba con `Toast`:

```kotlin
// En MainActivity.kt durante la fase previa de desarrollo:
recyclerView.adapter = HoroscopeAdapter(HoroscopeProvider.horoscopeList) { horoscope ->
    // Verificación previa en pantalla mediante Toast:
    val nombre = getString(horoscope.name)
    Toast.makeText(this, "Has seleccionado: $nombre", Toast.LENGTH_SHORT).show()
}
```

Una vez verificado que el `Toast` aparece correctamente con el nombre del signo al pulsar cualquier celda, reemplazamos la línea del `Toast` por el `Intent` de navegación hacia la `DetailActivity`.

---

## Los 4 Pasos de la Navegación Completa

### Paso 1: Crear la nueva Activity y su Layout

1. Crear la clase Kotlin: `DetailActivity.kt` extendiendo de `AppCompatActivity()`.
2. Crear el diseño XML: `res/layout/activity_detail.xml`.

---

### Paso 2: Registrar la Activity en `AndroidManifest.xml`

Toda nueva Activity **debe estar registrada** en el manifiesto para que el sistema Android permita abrirla:

```xml
<manifest ...>
    <application ...>

        <!-- Pantalla principal -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            ...
        </activity>

        <!-- Nueva pantalla de detalle -->
        <activity
            android:name=".DetailActivity"
            android:exported="false" />

    </application>
</manifest>
```

> **Nota**: `exported="false"` indica que esta pantalla es de uso interno de la aplicación.

---

### Paso 3: Capturar el Clic e Iniciar la Navegación (`MainActivity.kt` / `HoroscopeAdapter.kt`)

Al hacer clic en una celda de la lista, creamos un `Intent`, adjuntamos el ID del signo con `putExtra()`, y llamamos a `startActivity()`:

```kotlin
// Dentro de MainActivity.kt al configurar el Adapter:
recyclerView.adapter = HoroscopeAdapter(HoroscopeProvider.horoscopeList) { horoscope ->

    // 1. Crear el Intent definiendo el origen (this) y el destino (DetailActivity::class.java)
    val intent = Intent(this, DetailActivity::class.java).apply {
        // 2. Adjuntar información (extra) mediante clave-valor
        putExtra(DetailActivity.EXTRA_HOROSCOPE_ID, horoscope.id)
    }

    // 3. Iniciar la nueva Activity
    startActivity(intent)
}
```

---

### Paso 4: Recibir los Datos y Regresar (`DetailActivity.kt`)

En la pantalla de destino, leemos el dato enviado con `intent.getStringExtra()` y mostramos la información correspondiente:

```kotlin
class DetailActivity : AppCompatActivity() {

    companion object {
        // Clave constante para identificar el dato en los extras
        const val EXTRA_HOROSCOPE_ID = "extra_horoscope_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Leer el extra enviado
        val horoscopeId = intent.getStringExtra(EXTRA_HOROSCOPE_ID) ?: ""

        // 2. Buscar la información completa usando el ID
        val horoscope = HoroscopeProvider.getHoroscopeById(horoscopeId)

        if (horoscope != null) {
            // Asignar los datos a las vistas...
            tvName.text = getString(horoscope.name)
            tvDetailText.text = getString(horoscope.detail)
        }

        // 3. Regresar a la pantalla anterior al presionar el botón de volver
        btnBack.setOnClickListener {
            finish()
        }
    }
}
```

---

## Flujo Completo de Navegación

```text
[ MainActivity / RecyclerView ]
           │
  (Usuario hace clic en Aries)
           │
           ▼
[ Intent explícito con extra: EXTRA_HOROSCOPE_ID = "aries" ]
           │
           ▼
[ startActivity(intent) ]
           │
           ▼
[ DetailActivity ]
   ├── Lee extra ("aries")
   ├── Busca datos en HoroscopeProvider
   └── Muestra detalle e ícono de Aries
           │
  (Usuario pulsa "← Volver")
           │
           ▼
[ finish() ──► Cierra DetailActivity y vuelve a MainActivity ]
```
