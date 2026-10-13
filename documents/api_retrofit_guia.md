# Guía de Conexión a API REST con Retrofit, Gson, Corrutinas y Google ML Kit

Esta guía explica paso a paso cómo conectar una aplicación Android a una API REST externa (**freehoroscopeapi.com**), deserializar respuestas JSON, ejecutar llamadas asíncronas con Corrutinas de Kotlin, traducir dinámicamente en el dispositivo con Google ML Kit y alternar entre periodos (Diario, Semanal, Mensual).

---

## 1. Conceptos Clave

1. **API REST**: Un servicio en la nube al que nuestra aplicación envía peticiones HTTP (ej. `GET`) para recibir datos en formato JSON.
2. **Retrofit**: La librería estándar en Android para realizar peticiones HTTP de forma rápida, limpia y segura.
3. **Gson**: Librería convertidora que transforma automáticamente las cadenas JSON en objetos Kotlin (`Data Classes`).
4. **Corrutinas (`suspend`)**: Mecanismo de Kotlin para ejecutar tareas asíncronas de red en segundo plano sin congelar o bloquear la pantalla principal (*UI Thread*).
5. **Google ML Kit On-Device Translation**: Librería oficial de Google para traducir texto de un idioma a otro directamente en el procesador del teléfono de forma gratuita y sin servidores.

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

    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/daily")
    suspend fun getDailyHoroscope(@Query("sign") sign: String): Response<HoroscopeResponse>

    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/weekly")
    suspend fun getWeeklyHoroscope(@Query("sign") sign: String): Response<HoroscopeResponse>

    @Headers("Accept: application/json")
    @GET("api/v1/get-horoscope/monthly")
    suspend fun getMonthlyHoroscope(@Query("sign") sign: String): Response<HoroscopeResponse>
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

### Paso 5: Consultar la API, Traducir en el Dispositivo y Validar la Fecha (`DetailActivity.kt`)

Ejecutamos la petición dentro de una corrutina, traducimos al español con Google ML Kit si el idioma activo es Español y mostramos el periodo seleccionado:

```kotlin
private fun fetchHoroscopeFromApi(horoscope: Horoscope, period: HoroscopePeriod = HoroscopePeriod.DAILY) {
    progressBar.visibility = View.VISIBLE

    // Iniciar corrutina asíncrona segura vinculada al ciclo de vida de la Activity
    lifecycleScope.launch {
        try {
            val response = when (period) {
                HoroscopePeriod.DAILY -> RetrofitClient.apiService.getDailyHoroscope(horoscope.id)
                HoroscopePeriod.WEEKLY -> RetrofitClient.apiService.getWeeklyHoroscope(horoscope.id)
                HoroscopePeriod.MONTHLY -> RetrofitClient.apiService.getMonthlyHoroscope(horoscope.id)
            }

            if (response.isSuccessful && response.body()?.data != null) {
                val apiData = response.body()!!.data!!
                val apiDate = apiData.date.orEmpty()
                val rawPrediction = apiData.horoscope.orEmpty()

                if (rawPrediction.isNotEmpty()) {
                    val currentLocales = AppCompatDelegate.getApplicationLocales()
                    val currentLanguage = if (currentLocales.isEmpty) {
                        resources.configuration.locales[0]?.language ?: "en"
                    } else {
                        currentLocales[0]?.language ?: "en"
                    }

                    // Si la app está en español, traducimos On-Device con Google ML Kit
                    val finalPrediction = if (currentLanguage == "es") {
                        TranslationManager.translateEnToEs(rawPrediction)
                    } else {
                        rawPrediction
                    }

                    tvDetailText.text = finalPrediction
                    tvApiStatus.text = "🟢 Predicción actualizada de la API ($apiDate)"
                } else {
                    tvApiStatus.text = "🟡 La fecha de la API difiere de la del dispositivo."
                }
            }
        } catch (e: Exception) {
            tvApiStatus.text = "⚪ Sin conexión a la API. Mostrando datos locales."
        } finally {
            progressBar.visibility = View.GONE
        }
    }
}
```

---

## 3. Traducción On-Device en el Dispositivo (Google ML Kit)

Dado que la API `freehoroscopeapi.com` devuelve la predicción **únicamente en inglés**, utilizamos la librería oficial **Google ML Kit On-Device Translation** (`com.google.mlkit:translate`) para traducir el texto al español en el propio teléfono del usuario de forma rápida, gratuita y privada.

### Ventajas de la Traducción On-Device:
1. **Totalmente Gratuita**: No requiere claves de API de pago ni cuotas de servidor.
2. **Funcionamiento Local**: Tras descargar el modelo de lenguaje ligero la primera vez, traduce localmente en el procesador del teléfono.
3. **Soporte Offline**: Una vez descargado el modelo, funciona incluso sin conexión a internet.

---

## 4. Cómo Funcionan las Corrutinas de Kotlin en el Proyecto

Las **Corrutinas de Kotlin** son la solución oficial recomendada por Google para gestionar tareas asíncronas y de red en Android.

### Conceptos Clave de Corrutinas Utilizados:

1. **`suspend fun` (Funciones de Suspensión)**:
   - Una función marcada con `suspend` le indica al compilador que la tarea puede pausarse temporalmente en un hilo secundario (ej. E/S de red) y reanudarse cuando termine, **sin bloquear jamás el hilo de la interfaz de usuario (*UI Thread*)**.
   - Ejemplo en [HoroscopeApiService.kt](file:///Users/Mananas/Develops/horoscopo/app/src/main/java/com/juanmatoro/horoscopoandroid/data/api/HoroscopeApiService.kt):
     ```kotlin
     suspend fun getDailyHoroscope(@Query("sign") sign: String): Response<HoroscopeResponse>
     ```

2. **`lifecycleScope.launch` (Ámbito Seguro del Ciclo de Vida)**:
   - Es el "lanzador" de corrutinas integrado en las Activities de Android.
   - Garantiza la **cancelación automática**: Si el usuario cierra `DetailActivity` antes de que la red responda, la corrutina se cancela al instante para evitar consumir batería o provocar fugas de memoria (*Memory Leaks*).
   - Ejemplo en [DetailActivity.kt](file:///Users/Mananas/Develops/horoscopo/app/src/main/java/com/juanmatoro/horoscopoandroid/activities/DetailActivity.kt):
     ```kotlin
     lifecycleScope.launch {
         // Código asíncrono no bloqueante
     }
     ```

3. **`.await()` (Integración de Tasks de Google con Corrutinas)**:
   - La librería `kotlinx.coroutines.play.services` provee la función de extensión `.await()`, la cual transforma las promesas/tareas asíncronas de Google ML Kit en funciones de suspensión compatibles con Corrutinas de Kotlin.
   - Ejemplo en [TranslationManager.kt](file:///Users/Mananas/Develops/horoscopo/app/src/main/java/com/juanmatoro/horoscopoandroid/utils/TranslationManager.kt):
     ```kotlin
     translator.downloadModelIfNeeded().await() // Suspende hasta completar la descarga
     translator.translate(text).await()         // Suspende hasta completar la traducción
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
[ Muestra ProgressBar y Selector (Diario, Semanal, Mensual) ]
              │
              ▼
[ Petición de red asíncrona mediante Corrutina: GET /api/v1/get-horoscope/* ]
              │
              ├──────────────────────────────────┐
              ▼                                  ▼
[ Respuesta exitosa HTTP 200 ]          [ Error de red / Sin internet ]
              │                                  │
              ▼                                  ▼
[ ¿Fecha API válida? ]                  [ Oculta ProgressBar y muestra ]
      │                   │             [ aviso de modo fuera de línea ]
      │ Sí                │ No
      ▼                   ▼
[ ¿Idioma app es Español ("es")? ]      [ Mantiene texto local ]
      │                   │
      │ Sí                │ No
      ▼                   ▼
[ Traduce On-Device    [ Muestra texto ]
  con Google ML Kit ]   [ en inglés    ]
      │
      ▼
[ Muestra predicción traducida en vivo ]
```
