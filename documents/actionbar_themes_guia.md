# Guía de ActionBar, Toolbar, Temas y Modo Oscuro en Android

En esta guía aprenderemos cómo funcionan la **ActionBar**, la **Toolbar**, los **Temas (Themes)**, la adaptación al **Modo Oscuro (Dark Mode)** y la creación manual de **Menús** en Android.

---

## 1. ActionBar vs Toolbar: ¿Cuál es la diferencia?

| Componente | Descripción |
| :--- | :--- |
| **ActionBar** | La barra de título tradicional de Android. La administra el sistema automáticamente en la parte superior de la ventana. Es poco flexible. |
| **Toolbar** | El componente moderno de Material Design (`androidx.appcompat.widget.Toolbar`). Se coloca directamente dentro del XML como una vista más, ofreciendo control total sobre su posición, animación, colores e íconos. |

> **Práctica Recomendada Actual**: Utilizar el tema `Theme.Material3.DayNight.NoActionBar` en el archivo de temas e incluir una `Toolbar` o cabecera personalizada en el diseño XML.

---

## 2. Gestión de Temas: `values/themes.xml` y `values-night/themes.xml`

Android organiza los temas visuales en dos carpetas dentro de `res/`:

```text
res/
 ├── values/
 │    ├── colors.xml     <-- Colores base del tema claro
 │    └── themes.xml     <-- Estilos para el Modo Claro (Light Mode)
 └── values-night/
      ├── colors.xml     <-- (Opcional) Colores del tema oscuro
      └── themes.xml     <-- Estilos para el Modo Oscuro (Dark Mode)
```

---

## 3. Atributos Principales de Color en un Tema Material

En `themes.xml` definimos la paleta de colores usando atributos estándar:

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Tema Base de la Aplicación -->
    <style name="Base.Theme.HoroscopoAndroid" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Color principal para botones y barras -->
        <item name="colorPrimary">@color/color_es</item>
        <!-- Color de texto e íconos sobre el colorPrimary -->
        <item name="colorOnPrimary">@color/white</item>
        <!-- Color de fondo general de las pantallas -->
        <item name="android:windowBackground">@color/light_turquoise</item>
    </style>

    <style name="Theme.HoroscopoAndroid" parent="Base.Theme.HoroscopoAndroid" />
</resources>
```

---

## 4. Adaptación al Modo Oscuro (Dark Theme / Night Mode)

### ¿Cómo funciona la selección automática?
Cuando el usuario activa el **Modo Oscuro** en los ajustes del teléfono, Android busca automáticamente la carpeta `values-night/themes.xml` y aplica los colores adecuados para proteger la vista en ambientes oscuros.

#### Ejemplo en `res/values-night/themes.xml` (Modo Oscuro):
```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Base.Theme.HoroscopoAndroid" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Colores adaptados para contraste oscuro -->
        <item name="colorPrimary">#80CBC4</item>
        <item name="colorOnPrimary">#000000</item>
        <item name="android:windowBackground">#121212</item>
    </style>
</resources>
```

---

### Controlar el Modo Oscuro por Código

Podemos cambiar entre Modo Claro y Modo Oscuro usando `AppCompatDelegate`:

```kotlin
// 1. Activar Modo Oscuro (Night Mode)
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

// 2. Activar Modo Claro (Day Mode)
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

// 3. Seguir automáticamente la configuración del sistema operativo (Recomendado)
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
```

---

## 5. Implementación de una `Toolbar` en el XML y Kotlin

Si deseas usar una `Toolbar` como ActionBar oficial en tu pantalla:

### En el archivo XML (`activity_main.xml`):
```xml
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="?attr/colorPrimary"
    app:title="@string/app_name"
    app:titleTextColor="?attr/colorOnPrimary" />
```

### En el código Kotlin (`MainActivity.kt`):
```kotlin
val toolbar: Toolbar = findViewById(R.id.toolbar)
// Establece la Toolbar como la ActionBar oficial de la Activity
setSupportActionBar(toolbar)

// Habilitar el botón de regreso (flecha atrás) en una pantalla de detalle
supportActionBar?.setDisplayHomeAsUpEnabled(true)
```

---

## 6. Crear Menús Manualmente en Android Studio (Paso a Paso)

A continuación explicamos el proceso completo para crear y vincular un menú superior en la barra de opciones.

### Paso 1: Crear la carpeta de recursos `menu` en Android Studio

En el panel del proyecto de la izquierda en Android Studio:

```text
[ Panel Android ]
       │
       └── app/
            └── res/ (Hacer clic derecho sobre la carpeta res)
                 │
                 ├── New ──► Android Resource Directory
                 │               │
                 │               ├── Resource type: Seleccionar "menu"
                 │               └── Directory name: Escribir "menu"
                 │               └── Click en OK
```

---

### Paso 2: Crear el archivo XML del Menú (`main_menu.xml`)

Una vez creada la carpeta `res/menu/`:

```text
[ Carpeta res/menu/ ] (Hacer clic derecho)
       │
       └── New ──► Menu Resource File
                     │
                     ├── File name: Escribir "main_menu.xml"
                     └── Click en OK
```

---

### Paso 3: Definir las opciones del Menú en XML (`main_menu.xml`)

Abre el archivo `res/menu/main_menu.xml` y define los elementos `<item>`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <!-- Opción de Búsqueda (Visible directamente con icono) -->
    <item
        android:id="@+id/action_search"
        android:icon="@android:drawable/ic_menu_search"
        android:title="@string/action_search"
        app:showAsAction="ifRoom" />

    <!-- Opción de Ajustes (Dentro del menú desplegable de 3 puntos) -->
    <item
        android:id="@+id/action_settings"
        android:title="@string/action_settings"
        app:showAsAction="never" />

</menu>
```

#### Explicación del atributo `app:showAsAction`:
- **`always`**: Muestra siempre el ícono en la barra.
- **`ifRoom`**: Muestra el ícono si hay espacio suficiente en la pantalla.
- **`never`**: Coloca la opción ocultar dentro del menú desplegable (Overflow Menu de 3 puntos verticales).

---

### Paso 4: Inflar (Cargar) el Menú en la Activity (`MainActivity.kt`)

Sobrescribir el método `onCreateOptionsMenu`:

```kotlin
override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    // Carga e infla el diseño XML del menú dentro de la barra
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
}
```

---

### Paso 5: Responder a los clics sobre las opciones (`onOptionsItemSelected`)

Sobrescribir el método `onOptionsItemSelected` para capturar cuándo el usuario pulsa un ítem:

```kotlin
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.action_search -> {
            // Acción al pulsar el botón de buscar
            Toast.makeText(this, "Búsqueda seleccionada", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_settings -> {
            // Acción al pulsar Ajustes en el menú desplegable
            Toast.makeText(this, "Ajustes seleccionados", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
```

---

## Resumen de Buenas Prácticas

1. **Usar siempre temas `NoActionBar`**: Te da control total para personalizar la cabecera.
2. **Usar atributos de color (`?attr/colorPrimary`)**: En lugar de colores estáticos (`#FFFFFF`), usa referencias del tema para que se adapten automáticamente al cambiar a Modo Oscuro.
3. **Ofrecer `MODE_NIGHT_FOLLOW_SYSTEM`**: Es la experiencia de usuario más respetuosa con la configuración del dispositivo.
4. **Organizar Menús en XML (`res/menu/`)**: Mantiene separada la estructura visual de las opciones de la lógica Kotlin.
