# Guía sobre el Contexto (Context) en Android

El **`Context`** es uno de los conceptos más importantes y fundamentales en el desarrollo Android.

---

## 1. ¿Qué es el Contexto (`Context`)?

En términos sencillos, el **`Context`** es el "puente" o manija (*handle*) que el sistema operativo Android le otorga a la aplicación para que pueda interactuar con el entorno del dispositivo.

Sin un `Context`, una clase Kotlin normal no tiene acceso al sistema Android.

### ¿Para qué se utiliza el `Context`?
- **Acceder a recursos**: Leer cadenas (`getString(R.string.*)`), colores (`getColor(R.color.*)`) e imágenes.
- **Abrir pantallas**: Iniciar una nueva `Activity` mediante `startActivity(intent)`.
- **Mostrar avisos e interfaz**: Crear `Toast`, diálogos y desplegar layouts XML.
- **Acceder al almacenamiento local**: Abrir archivos de `SharedPreferences` o bases de datos (Room).
- **Acceder a servicios del sistema**: Obtener información de red, ubicación, vibración, etc.

---

## 2. Tipos Principales de Contexto

En Android existen principalmente dos tipos de contexto según su tiempo de vida (ciclo de vida):

| Tipo de Contexto | Ámbito / Duración | Cómo se obtiene | Para qué se usa |
| :--- | :--- | :--- | :--- |
| **Activity Context** | **Temporal**: Vive únicamente mientras la pantalla (`Activity`) está abierta. | Mediante **`this`** dentro de una Activity, o **`itemView.context`** dentro de un ViewHolder. | • Inflar layouts.<br>• Mostrar `Toast` o diálogos.<br>• Iniciar navegación hacia otra Activity. |
| **Application Context** | **Global**: Vive durante todo el ciclo de vida de la aplicación en memoria. | Mediante **`applicationContext`** o **`context.applicationContext`**. | • Almacenamiento con `SharedPreferences`.<br>• Inicializar bases de datos (Room).<br>• Inicializar librerías globales. |

---

## 3. Fugas de Memoria (Memory Leaks) y el Contexto

Una **fuga de memoria (*Memory Leak*)** ocurre cuando el recolector de basura (*Garbage Collector*) de Android no puede liberar la memoria de una pantalla (`Activity`) que el usuario ya cerró, porque alguien conserva una referencia guardada hacia su `Context`.

### ⚠️ Regla de Oro:
**NUNCA guardes una referencia de un `Activity Context` en una variable estática, un `object` singleton o una clase de larga duración.**

#### ❌ MAL (Causa fuga de memoria):
```kotlin
object MySingleton {
    // ❌ ERROR: Guardar la Activity en un singleton impedirá que Android libere la pantalla al cerrarse.
    var savedContext: Context? = null 
}
```

#### ✅ BIEN (Práctica correcta):
```kotlin
object FavoriteManager {
    // ✅ CORRECTO: El contexto se recibe como parámetro puntual en la función y no se almacena.
    fun saveFavorite(context: Context, horoscopeId: String) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE) ...
    }
}
```

---

## 4. Ejemplos Prácticos en Nuestro Proyecto

### Ejemplo 1: Pasando `this` desde las Activities
Dentro de `MainActivity.kt` o `DetailActivity.kt`, la palabra clave `this` representa la `Activity` actual (que es una subclase de `Context`):

```kotlin
// 'this' entrega el contexto necesario para leer SharedPreferences en FavoriteManager
val favoriteId = FavoriteManager.getFavorite(this)
```

---

### Ejemplo 2: Usando `itemView.context` en el ViewHolder
Dentro de un `RecyclerView.ViewHolder` (`HoroscopeAdapter.kt`), accedemos al contexto de la vista de la celda mediante `itemView.context`:

```kotlin
fun render(horoscope: Horoscope) {
    val context = itemView.context
    
    // El contexto nos permite obtener los textos traducidos según el idioma actual
    tvName.text = context.getString(horoscope.name)
    
    // Y obtener los colores definidos en el tema
    val color = ContextCompat.getColor(context, horoscope.type.colorRes)
}
```

---

### Ejemplo 3: Funciones de Extensión sobre `Context` (`ContextUtils.kt`)

Kotlin permite agregar funciones útiles directamente sobre la clase `Context`:

```kotlin
// Función de extensión que simplifica el uso de Toast en cualquier Context
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Uso directo en cualquier Activity o Context:
showToast("¡Mensaje de prueba!")
```

---

## 5. Uso de `object` (Singleton) y el Contexto: El caso de `FavoriteManager`

### ¿Por qué `FavoriteManager` es un `object` y no una `class`?

En Kotlin, la palabra clave **`object`** define un **Singleton** (una única instancia global creada automáticamente por el lenguaje).

1. **Instancia Única**: No necesitamos crear múltiples objetos con `FavoriteManager()`. Solo queremos un punto de acceso único para gestionar el favorito guardado en `SharedPreferences`.
2. **Llamadas Directas**: Al ser un `object`, cualquier pantalla puede invocar sus métodos directamente (`FavoriteManager.saveFavorite(this, "virgo")`) de forma concisa.
3. **Eficiencia**: Evitamos instanciar y destruir objetos innecesarios en memoria RAM.

### ¿Cómo se relaciona un `object` con el `Context` sin causar Fugas de Memoria?

Como un `object` singleton vive durante toda la ejecución de la aplicación, **nunca debemos guardar un `Context` en sus propiedades**. 

En lugar de guardarlo en una propiedad, pasamos `context: Context` como **parámetro temporal** en cada función:

```kotlin
object FavoriteManager {
    // ✅ EL CONTEXTO SE RECIBE Y SE USA SOLO DURANTE LA EJECUCIÓN DE LA FUNCIÓN
    fun saveFavorite(context: Context, horoscopeId: String) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("key_favorite_horoscope_id", horoscopeId)
            .apply()
    }
}
```

De esta forma, cuando la pantalla se cierra, la referencia al `Context` se libera inmediatamente sin provocar fugas de memoria (*Memory Leaks*).

---

## Resumen de Buenas Prácticas

1. **Usa el contexto de vida más corto posible** que cumpla la función.
2. **Usa `this` (Activity Context)** para tareas relacionadas con la interfaz visual (`Toast`, navegación, diálogos).
3. **Usa `applicationContext`** para operaciones de fondo o bases de datos globales.
4. **Pasa el contexto como parámetro de función** en lugar de guardarlo en propiedades de clases u objetos singleton.
