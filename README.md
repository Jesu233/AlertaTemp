# AppAlertaGalpones_GrupoX – DSY1105 (Evaluación Parcial 2)

App Android (Kotlin + Jetpack Compose, MVVM) del caso **"Alerta temprana por altas temperaturas"**
(empresa avícola y ganadera, pollos de engorda). Operarios, supervisores y jefaturas consultan desde el celular
la temperatura y humedad de cada galpón, reciben notificaciones críticas, revisan el historial y registran acciones.
Todos los datos y usuarios son **ficticios**.

**Integrantes:** _Nombre Apellido_ · _Nombre Apellido_ — **Equipo N°:** __

## Cómo ejecutar
1. Android Studio reciente, JDK 17. `File > Open` → carpeta del proyecto → Gradle Sync.
2. Versiones: AGP 9.1.1, Gradle 9.3.1, compileSdk 37 (lo exigen navigation 2.10.2 y lifecycle 2.11.0 de las guías). AGP 9 incluye Kotlin, por eso no se usa el plugin `kotlin-android`. Necesitas el SDK Platform 37 instalado (SDK Manager).
3. Ejecutar en emulador o dispositivo (API 26+).
4. Usuarios ficticios (clave `Galpon123`): `operario1`, `operario2`, `operario3`, `supervisor1`, `supervisor2`, `jefatura1`.

## Estructura (igual a las Guías 9–14)
```
com.example.appalertagalpones
├── MainActivity.kt              Crea BD, repositorios y fábrica de ViewModels (Guía 14)
├── data
│   ├── model                    Entidades Room y modelos: Galpon, Medicion, Alerta, AccionRegistrada, Usuario, Estado (Guía 10)
│   ├── remote                   MonitoreoApi + FakeMonitoreoApi (API simulada)
│   ├── MonitoreoDao / AppDatabase / MonitoreoRepository     Room (Guía 14)
│   ├── PreferenciasRepository   DataStore: sesión y umbrales (Guía 12)
│   └── FotoStorage              Evidencia en almacenamiento interno
├── ui
│   ├── navigation               AppRoutes + AppNavigation: Scaffold, TopAppBar, NavigationBar, Drawer (Guía 10)
│   ├── screen                   Pantallas Compose
│   ├── viewmodel                ViewModels con StateFlow, UiState y validarFormulario() (Guías 11–12), fábrica
│   ├── components               CampoTexto, SelectorDesplegable, GraficoLinea
│   └── theme
└── util                         Notificador, formatos
```

## Qué guía aplica dónde
| Guía | Aplicación |
|---|---|
| 9 Adaptabilidad | `BoxWithConstraints`: grilla 1/2 columnas (Galpones), detalle y panel lado a lado desde 600 dp |
| 10 Navegación | `AppRoutes`, `NavHost`, `Scaffold`, `TopAppBar` con título por ruta, `NavigationBar`, `ModalNavigationDrawer`; las pantallas reciben solo lambdas, el ViewModel no conoce el `NavController` |
| 11 Formularios | `LoginUiState/LoginErrores`, `validarFormulario()`, `Checkbox`, ViewModels compartidos sin pasar argumentos por ruta |
| 12 Estado y DataStore | `StateFlow` + `collectAsStateWithLifecycle`, `PreferenciasRepository`, `animate*AsState` |
| 13 Cámara y galería | `TakePicturePreview` + `GetContent` + Coil (`ImagenUiState`), sin permisos extra |
| 14 Room/SQLite | Entity, DAO, Database, Repository, fábrica de ViewModel; flujo Usuario → UI → ViewModel → Repository → DAO |

## Qué pregunta del caso resuelve cada función
| Pregunta | Implementación |
|---|---|
| 1 Rangos | Umbrales ficticios y configurables de temperatura **y humedad** (pantalla Umbrales), edad de las aves visible en cada galpón |
| 2 Acciones | Recomendación según estado; alerta crítica prioritaria con notificación; registro de acciones simuladas |
| 3 Unidades | °C y %, fecha y hora de medición, colores e íconos por estado |
| 4 Destinatarios | Operarios/supervisores ven y reciben notificaciones solo de sus galpones; la jefatura usa el Panel general |
| 5 Simulación | Pantalla **Simulador**: fija temperatura/humedad, "forzar alerta crítica", restablecer |
| 6 Historial de alertas | Galpón, fecha/hora, medición, criticidad, estado de atención (Pendiente → En atención → Atendida); filtros por tipo, fecha y galpón |
| 7 Fundos y sectores | 2 fundos, 2 sectores, 6 galpones de pollos de engorda (cantidades a validar con el docente) |
| 8 Contacto | Pantalla Contactos y tarjeta de responsables con botón "Llamar" (marcador del teléfono) |
| 9 Comunicación interna | No incluida (no es MVP) |
| 10 Mediciones automáticas | Medición simulada cada 1 min (`GalponesViewModel.INTERVALO_MS`) guardada en Room |
| 11 Gráficos | Temperatura y humedad por período (30 min, 2 h, todo) |
| 12 Trazabilidad | Cada acción guarda usuario, galpón, fecha, hora y tipo; el operario ve las suyas, supervisor y jefatura todas |
| 13–14 Turnos y accesos | Visibilidad por rol; jefatura en solo lectura. Historial de turnos no incluido |
| 15 Android | Solo Android, API simulada, datos recientes guardados localmente |

No incluido (extras opcionales según el docente): exportar reportes a PDF/Excel, mensajería directa, historial de turnos.

## Rúbrica
| Indicador | Evidencia |
|---|---|
| IE 2.1.1 | `AppNavigation.kt`, `ui/theme`, diseño adaptable |
| IE 2.1.2 | Formularios: Login, Registrar acción, Umbrales, Simulador (ícono, error y validado por campo) |
| IE 2.2.1 | `Validadores.kt` + `validarFormulario()` en ViewModels |
| IE 2.2.2 | Color de estado, contadores, rotación de refresco, gráfico, transiciones, Crossfade |
| IE 2.3.1 | MVVM, Room, DataStore |
| IE 2.3.2 | GitHub y Trello (`docs/PLAN_GIT_TRELLO.md`) |
| IE 2.4.1 | Notificaciones, cámara y galería, marcador telefónico |
