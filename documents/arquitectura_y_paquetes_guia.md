# Guía de Organización de Proyectos y Patrones de Arquitectura en Android

Esta guía explica cómo estructurar paquetes en proyectos Android y cuáles son los patrones de arquitectura más comunes, indicando cuándo es conveniente utilizar cada uno.

---

## 1. Organización de Proyectos por Paquetes

Existen dos estrategias principales para organizar las carpetas (paquetes) dentro de un proyecto:

### A. Organización por Capas (Package by Layer) — *Estructura de nuestro proyecto*

Consiste en agrupar los archivos según el rol que desempeñan dentro de la aplicación:

```text
com.juanmatoro.horoscopoandroid/
 ├── activities/    <-- Pantallas de la app (MainActivity, DetailActivity)
 ├── adapters/      <-- Adaptadores para RecyclerViews (HoroscopeAdapter)
 ├── data/          <-- Modelos, gestores de almacenamiento y proveedores (Horoscope, FavoriteManager)
 └── utils/         <-- Extensiones y utilidades reutilizables (ContextUtils, DateUtils)
```

- **¿Cuándo usarlo?**:
  - Proyectos pequeños y medianos.
  - Aplicaciones de aprendizaje, prototipos o componentes simples.
  - Facilita encontrar rápidamente un tipo de componente (ej. buscar todos los adapters).

---

### B. Organización por Funcionalidad (Package by Feature)

Consiste en agrupar los archivos según la funcionalidad o pantalla de la app:

```text
com.juanmatoro.horoscopoandroid/
 ├── horoscope_list/    <-- Todo lo necesario para la lista (Activity, Adapter)
 ├── horoscope_detail/  <-- Todo lo necesario para el detalle
 ├── favorites/         <-- Gestión de favoritos
 └── core/              <-- Utilidades compartidas en toda la app
```

- **¿Cuándo usarlo?**:
  - Aplicaciones grandes con múltiples módulos o características independientes.
  - Equipos de desarrollo grandes donde varios programadores trabajan en funciones distintas simultáneamente.

---

## 2. Los Patrones de Arquitectura Más Comunes en Android

Una arquitectura define **cómo se dividen las responsabilidades** entre las clases para lograr un código limpio, mantenible y fácil de probar.

---

### A. MVVM (Model - View - ViewModel) — *El estándar recomendado por Google*

Separación clara en tres componentes principales:

```text
[ View (Activity/Fragment/Compose) ]
              │  ▲
   Eventos UI │  │ Estado (StateFlow/LiveData)
              ▼  │
      [ ViewModel ]
              │  ▲
      Lógica  │  │ Datos
              ▼  │
    [ Model (Repository/Room/Retrofit) ]
```

- **View**: Solo se encarga de pintar la pantalla y enviar eventos del usuario.
- **ViewModel**: Procesa la lógica de presentación y mantiene el estado. **Sobrevive a cambios de configuración** (como rotar la pantalla).
- **Model**: Maneja los datos (base de datos local Room, llamadas a API con Retrofit o SharedPreferences).

#### ¿Para qué es lo más indicado?
- **El 90% de aplicaciones modernas en Android**.
- Proyectos con datos dinámicos, llamadas de red o bases de datos.
- Aplicaciones que utilizan **Jetpack Compose** o **View Binding**.

---

### B. MVI (Model - View - Intent) — *Flujo de estado unidireccional (UDF)*

Variante reactiva donde la interfaz emite intenciones (*Intents*) y recibe un único estado inmutable (*UiState*):

```text
[ View ] ──► Intent (Acción) ──► [ Model / ViewModel ] ──► UiState ──► [ View ]
```

- **¿Para qué es lo más indicado?**:
  - Aplicaciones con pantallas complejas y muchos estados posibles.
  - Proyectos 100% reactivos con **Kotlin Coroutines** y **StateFlow**.
  - Facilita el testeo unitario y la reproducción exacta de errores.

---

### C. MVC (Model - View - Controller) — *Patrón tradicional*

- **Model**: Datos de la app.
- **View**: Diseños XML.
- **Controller**: La `Activity` actúa como controlador manejando tanto la vista como la lógica.

- **¿Para qué es lo más indicado?**:
  - Prototipos extremadamente sencillos o aplicaciones básicas de aprendizaje.
  - *Desventaja*: Tiende a sobrecargar la `Activity` con demasiadas líneas de código ("God Activity").

---

### D. MVP (Model - View - Presenter)

- La `View` define una interfaz (contrato) y se la entrega al `Presenter`, el cual gestiona la lógica y ordena a la vista qué pintar.

- **¿Para qué es lo más indicado?**:
  - Proyectos existentes en Java/Kotlin que no utilizan los ViewModels de Android Jetpack.

---

## 3. Clean Architecture (Arquitectura Limpia)

Cuando una aplicación crece significativamente, suele combinarse **MVVM** con **Clean Architecture**, dividiendo el código en 3 capas concéntricas:

```text
┌─────────────────────────────────────────────────────────┐
│  Presentation Layer (Activities, ViewModels, Adapters)  │
├─────────────────────────────────────────────────────────┤
│  Domain Layer       (UseCases, Entidades de Negocio)    │
├─────────────────────────────────────────────────────────┤
│  Data Layer         (Repositories, Room, Retrofit, Prefs)│
└─────────────────────────────────────────────────────────┘
```

- **Presentation Layer**: Interfaz de usuario.
- **Domain Layer**: Reglas de negocio puras (no depende de Android).
- **Data Layer**: Acceso a datos locales y remotos.

---

## Tabla Comparativa y Recomendación

| Patrón / Estructura | Complejidad | Escalabilidad | Caso de Uso Ideal |
| :--- | :--- | :--- | :--- |
| **Capas + MVC** | Baja | Baja | Apps de aprendizaje, prototipos o listas sencillas. |
| **MVVM (Estándar)** | Media | Alta | Aplicaciones profesionales de tamaño pequeño a grande. |
| **MVVM + Clean Architecture** | Alta | Muy Alta | Aplicaciones empresariales, bancos o redes sociales. |
| **MVI** | Alta | Muy Alta | Pantallas con estados complejos y flujos reactivos. |
