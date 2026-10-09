# Guía de Conexión a API REST con Retrofit, Gson y Corrutinas en Android

Esta guía explica paso a paso cómo conectar una aplicación Android a una API REST externa (**freehoroscopeapi.com**), deserializar las respuestas JSON y validar la frescura de los datos comparando fechas.

---

## 1. Conceptos Clave

1. **API REST**: Un servicio en la nube al que nuestra aplicación envía peticiones HTTP (ej. `GET`) para recibir datos en formato JSON.
2. **Retrofit**: La librería estándar en Android para realizar peticiones HTTP de forma rápida, limpia y segura.
3. **Gson**: Librería convertidora que transforma automáticamente las cadenas JSON en objetos Kotlin (`Data Classes`).
4. **Corrutinas (`suspend`)**: Mecanismo de Kotlin para ejecutar tareas asíncronas de red en segundo plano sin congelar o bloquear la pantalla principal (*UI Thread*).

---

## 2. Los 5 Pasos para Conectar una API REST

### Paso 1: Declarar el Permiso de Internet (`AndroidManifest.xml`)

Para permitir que la app acceda a la red, agregamos el permiso de Internet:

```xml
<manifest ...>
    <!-- Permiso obligatorio para realizar llamadas HTTP a la API -->
    <uses-permission android:name="android.permission.INTERNET" />
    ...
</manifest>
```

---

### Paso 2: Crear el Modelo de Datos JSON (`HoroscopeResponse.kt`)

Mapeamos la estructura JSON entregada por el servidor `freehoroscopeapi.com`:

```json
{
  "data": {
    "date": "2026-10-09",
    "period": "daily",
    "sign": "Aries",
    "horoscope": "Aries, today you may feel a strong urge..."
  }
}
```

En Kotlin usamos `@SerializedName` para vincular cada clave del JSON con nuestra variable:

```kotlin
data class HoroscopeResponse(
    @SerializedName("data") val data: HoroscopeData?
)

data class HoroscopeData(
    @SerializedName("date") val date: String?,
    @SerializedName("period") val period: String?,
    @SerializedName("sign") val sign: String?,
    @SerializedName("horoscope") val horoscope: String?
)
```

---

### Paso 3: Definir la Interfaz de la API (`HoroscopeApiService.kt`)

Definimos los endpoints y parámetros mediante anotaciones de Retrofit:

```kotlin
interface HoroscopeApiService {

    // Realiza un GET a: https://freehoroscopeapi.com/api/v1/get-horoscope/daily?sign=aries
    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/daily")
    suspend fun getDailyHoroscope(
        @Query("sign") sign: String
    ): Response<HoroscopeResponse>
}
```

---

### Paso 4: Crear el Cliente Singleton (`RetrofitClient.kt`)

Configuramos la URL base y el convertidor de Gson en una única instancia reutilizable:

```kotlin
object RetrofitClient {

    private const val BASE_URL = "https://freehoroscopeapi.com/"

    val apiService: HoroscopeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HoroscopeApiService::class.java)
    }
}
```

---

### Paso 5: Consultar la API y Validar la Fecha (`DetailActivity.kt`)

Ejecutamos la petición dentro de una corrutina y comparamos la fecha del dispositivo con la devuelta por la API:

```kotlin
private fun fetchHoroscopeFromApi(horoscope: Horoscope) {
    progressBar.visibility = View.VISIBLE

    // Iniciar corrutina asíncrona en el ciclo de vida de la Activity
    lifecycleScope.launch {
        try {
            val response = RetrofitClient.apiService.getDailyHoroscope(horoscope.id)

            if (response.isSuccessful && response.body()?.data != null) {
                val apiData = response.body()!!.data!!
                val apiDate = apiData.date.orEmpty()
                val apiPrediction = apiData.horoscope.orEmpty()

                // Obtener fecha actual del dispositivo (ej. "2026-10-09")
                val currentDate = DateUtils.getCurrentFormattedDate("yyyy-MM-dd")

                // Validación de frescura: Comparamos fechas
                if (apiDate == currentDate && apiPrediction.isNotEmpty()) {
                    // Datos al día: Mostramos la predicción de la API
                    tvDetailText.text = apiPrediction
                    tvApiStatus.text = "🟢 Predicción actualizada de la API ($apiDate)"
                } else {
                    // Fecha desactualizada: Mantenemos la predicción guardada
                    tvApiStatus.text = "🟡 La fecha de la API difiere de la del dispositivo."
                }
            }
        } catch (e: Exception) {
            // Error de conexión o red: Mantenemos la predicción guardada
            tvApiStatus.text = "⚪ Sin conexión a la API. Mostrando datos locales."
        } finally {
            progressBar.visibility = View.GONE
        }
    }
}
```

---

## Flujo Completo de Funcionamiento

```text
[ Usuario abre DetailActivity ]
              │
              ▼
[ Muestra datos locales guardados como respaldo ]
              │
              ▼
[ Muestra ProgressBar de carga ]
              │
              ▼
[ Petición de red asíncrona: GET /api/v1/get-horoscope/daily?sign=aries ]
              │
              ├──────────────────────────────────┐
              ▼                                  ▼
[ Respuesta exitosa HTTP 200 ]          [ Error de red / Sin internet ]
              │                                  │
              ▼                                  ▼
[ ¿Fecha API == Fecha Dispositivo? ]    [ Oculta ProgressBar y muestra ]
      │                   │             [ aviso de modo fuera de línea ]
      │ Sí                │ No
      ▼                   ▼
[ Actualiza texto      [ Mantiene texto local ]
  de la predicción ]
```
