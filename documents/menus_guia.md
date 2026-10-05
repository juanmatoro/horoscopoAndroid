# Guía Completa de Creación y Manejo de Menús en Android

Los **Menús** en Android permiten agregar opciones de acción en la barra superior (**MaterialToolbar** / **ActionBar**), como botones de búsqueda, favoritos, cambio de idioma o menús desplegables (Overflow Menu).

---

## 1. Conceptos Básicos

- **`res/menu/`**: Carpeta especial de recursos donde se guardan los archivos XML que definen los botones y opciones del menú.
- **`<menu>`**: Etiqueta raíz del XML que contiene la lista de ítems.
- **`<item>`**: Cada uno de los botones u opciones dentro del menú.
- **`menuInflater`**: Objeto que transforma el XML en componentes visuales interactivos dentro de la `Toolbar`.

---

## 2. Proceso Paso a Paso para Crear un Menú

### Paso 1: Crear la carpeta de recursos `menu` en Android Studio

En el panel de proyecto de Android Studio (vista Android):

```text
[ Carpeta res/ ] (Clic Derecho)
       │
       └── New ──► Android Resource Directory
                     │
                     ├── Resource type: Seleccionar "menu"
                     └── Directory name: Escribir "menu"
```

---

### Paso 2: Crear el archivo de menú XML (`activity_main_menu.xml`)

Dentro de la carpeta `res/menu/` recién creada:

```text
[ Carpeta res/menu/ ] (Clic Derecho)
       │
       └── New ──► Menu Resource File
                     │
                     ├── File name: "activity_main_menu.xml"
                     └── Click en OK
```

---

### Paso 3: Estructura del XML del Menú

Abre `res/menu/activity_main_menu.xml` y define los elementos `<item>`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <!-- Opción de Búsqueda interactiva con SearchView -->
    <item
        android:id="@+id/action_search"
        android:icon="@android:drawable/ic_menu_search"
        android:title="@string/search_menu"
        app:actionViewClass="androidx.appcompat.widget.SearchView"
        app:showAsAction="collapseActionView|ifRoom" />

    <!-- Opción para abrir el Horóscopo Favorito (Icono de Estrella) -->
    <item
        android:id="@+id/action_favorite"
        android:icon="@android:drawable/btn_star_big_on"
        android:title="@string/action_favorite"
        app:showAsAction="ifRoom" />

    <!-- Opción de Cambio de Idioma (i18n) -->
    <item
        android:id="@+id/action_language"
        android:title="@string/btn_language_es"
        app:showAsAction="always" />

</menu>
```

#### Atributos Clave de `<item>`:

| Atributo | Función |
| :--- | :--- |
| `android:id` | Identificador único para capturar los clics en Kotlin (ej. `@+id/action_search`). |
| `android:title` | Texto traducible que describe la opción (`@string/search_menu`). |
| `android:icon` | Ícono gráfico que se muestra en la barra (`@android:drawable/ic_menu_search`). |
| `app:showAsAction` | Controla la visibilidad del botón en la barra superior. |
| `app:actionViewClass` | Permite incrustar componentes avanzados como un `SearchView`. |

#### Valores de `app:showAsAction`:
- **`always`**: Muestra siempre el botón visible en la barra.
- **`ifRoom`**: Muestra el botón si hay espacio disponible en pantalla.
- **`never`**: Mueve la opción al menú desplegable de 3 puntos (Overflow Menu).
- **`collapseActionView`**: Permite que componentes como `SearchView` se expandan y colapsen al hacer clic.

---

### Paso 4: Cargar (Inflar) el Menú en Kotlin (`onCreateOptionsMenu`)

En tu `Activity` (por ejemplo `MainActivity.kt`), sobrescribe el método `onCreateOptionsMenu`:

```kotlin
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // 1. Infla el archivo de menú XML e inserta los botones en la Toolbar
    menuInflater.inflate(R.menu.activity_main_menu, menu)

    // 2. Opcional: Configurar componentes especiales como un SearchView
    val searchItem = menu.findItem(R.id.action_search)
    val searchView = searchItem?.actionView as? SearchView
    searchView?.queryHint = getString(R.string.search_hint)

    searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = false

        override fun onQueryTextChange(newText: String?): Boolean {
            // Filtrar datos en tiempo real al escribir
            filterHoroscopes(newText.orEmpty())
            return true
        }
    })

    return true
}
```

---

### Paso 5: Capturar las Pulsaciones (`onOptionsItemSelected`)

Sobrescribe el método `onOptionsItemSelected` para responder cuando el usuario hace clic en cualquier opción:

```kotlin
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.action_search -> {
            // El usuario pulsó el botón de buscar
            true
        }
        R.id.action_favorite -> {
            // El usuario pulsó la estrella de favorito
            val favoriteId = FavoriteManager.getFavorite(this)
            if (favoriteId != null) {
                // Abrir pantalla del favorito
            }
            true
        }
        R.id.action_language -> {
            // El usuario pulsó cambiar de idioma (i18n)
            toggleLanguage()
            true
        }
        android.R.id.home -> {
            // El usuario pulsó la flecha atrás oficial de la Toolbar
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
```

---

## 3. Flujo de Funcionamiento

```text
[ Archivo XML: res/menu/activity_main_menu.xml ]
                       │
                       ▼  (menuInflater.inflate)
            [ onCreateOptionsMenu() ]
                       │
                       ▼  (Aparece en MaterialToolbar)
             [ Usuario hace clic ]
                       │
                       ▼
            [ onOptionsItemSelected() ]
                       │
                       ├── R.id.action_search   ──► Despliega SearchView
                       ├── R.id.action_favorite ──► Abre Horóscopo Favorito
                       └── R.id.action_language ──► Alterna Idioma (ES / EN)
```

---

## 4. Buenas Prácticas para Menús en Android

1. **Vincular la Toolbar primero**: Asegúrate de haber llamado a `setSupportActionBar(toolbar)` en `onCreate()` antes de usar menús.
2. **Usar `@string/` en los títulos**: Todos los textos de menú deben estar en `strings.xml` para permitir internacionalización (i18n).
3. **No saturar la barra**: Usa `ifRoom` o `never` para opciones secundarias para no amontonar íconos en pantallas pequeñas.
4. **Usar IDs claros**: Nombre descriptivos como `action_search`, `action_favorite`, `action_language`.
