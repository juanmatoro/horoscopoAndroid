# Ficha de Aprendizaje: Internacionalización (i18n) y Localización en Android

## ¿Qué es i18n y l10n?

- **i18n (Internationalization / Internacionalización)**: Es la técnica de estructurar la aplicación para que pueda soportar múltiples idiomas sin modificar el código fuente. Se abrevia **i18n** porque hay 18 letras entre la 'i' inicial y la 'n' final.
- **l10n (Localization / Localización)**: Es la adaptación específica de la app para un idioma o región concreta (traducciones, formatos de fecha, etc.).

---

## El Sistema Nativo de Android

En Android **no se utilizan librerías externas** para la internacionalización. El sistema operativo cuenta con un mecanismo nativo basado en carpetas de recursos con clasificadores de idioma.

### 1. Estructura de Carpetas de Recursos (`res/`)

Todas las traducciones se gestionan dentro de la carpeta `res/`:

```text
res/
 ├── values/
 │    └── strings.xml      <-- Idioma por defecto / Fallback (Inglés)
 ├── values-es/
 │    └── strings.xml      <-- Traducción para Español
 ├── values-fr/
 │    └── strings.xml      <-- Traducción para Francés (opcional)
 └── values-de/
      └── strings.xml      <-- Traducción para Alemán (opcional)
```

---

### 2. Regla de Coincidencia de Claves (`name`)

Para que Android asocie correctamente las traducciones, **el atributo `name` debe ser exactamente igual** en todos los archivos `strings.xml`.

#### Ejemplo en `res/values/strings.xml` (Inglés / Por defecto):
```xml
<resources>
    <string name="app_name">The Zolthan Horoscope</string>
    <string name="element_fire">Fire • Passionate, dynamic, and energetic</string>
</resources>
```

#### Ejemplo en `res/values-es/strings.xml` (Español):
```xml
<resources>
    <string name="app_name">El Horóscopo Zolthan</string>
    <string name="element_fire">Fuego • Apasionados, dinámicos y enérgicos</string>
</resources>
```

---

## Cómo Utilizar los Textos Localizados

### En diseños XML:
Usando el prefijo `@string/` seguido del nombre del recurso:
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/app_name" />
```

### En código Kotlin:
Usando la función `getString()` pasándole el ID del recurso:
```kotlin
// Android resuelve automáticamente el texto traducido según el idioma activo del dispositivo:
val nameText: String = context.getString(horoscope.name)
```

---

## Cambio de Idioma Programático desde la App (API Oficial)

Para permitir al usuario cambiar el idioma directamente presionando un botón (sin tener que ir a los Ajustes del sistema), Android ofrece la API nativa `AppCompatDelegate.setApplicationLocales()`:

```kotlin
val btnLanguage: Button = findViewById(R.id.btnLanguage)
btnLanguage.setOnClickListener {
    // 1. Consultar el idioma actual de la app
    val currentLocales = AppCompatDelegate.getApplicationLocales()
    val currentLanguage = currentLocales.get(0)?.language ?: "en"

    // 2. Alternar entre Español ("es") e Inglés ("en")
    val newLanguage = if (currentLanguage == "es") "en" else "es"

    // 3. Aplicar el nuevo idioma (recrea la Activity suavemente con las nuevas traducciones)
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLanguage))
}
```

---

## Buenas Prácticas

1. **Nunca escribir texto directo (*hardcoded strings*)** ni en Kotlin ni en XMLs de diseño.
2. **Utilizar marcadores de posición** cuando necesites concatenar variables:
   - XML: `<string name="welcome_message">Hola, %s!</string>`
   - Kotlin: `getString(R.string.welcome_message, userName)`
3. **Usar el Translations Editor de Android Studio**: Haces clic derecho en `strings.xml` y seleccionas **Open Translations Editor** para editar todas las traducciones en una vista de tabla muy cómoda.

---

## Cómo Probar las Traducciones en la App

1. **Mediante el Botón de Idioma**: Presiona el botón `ES / EN` en la barra superior de la app para cambiar de idioma de forma instantánea.
2. **Mediante los Ajustes del Sistema**:
   - Abre **Ajustes > Sistema > Idiomas y entradas > Idiomas** en el teléfono.
   - Arrastra **Español** o **Inglés** al primer lugar.
