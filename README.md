# App AlertaTemp – DSY1105 (Evaluación Parcial 2)

App Android (Kotlin + Jetpack Compose, MVVM) del caso **"Alerta temprana por altas temperaturas"**
(empresa avícola y ganadera, pollos de engorda). Operarios, supervisores y jefaturas consultan desde el celular
la temperatura y humedad de cada galpón, reciben notificaciones críticas, revisan el historial y registran acciones.

**Integrantes:** _Nombre Apellido_ · _Nombre Apellido_ — **Equipo N°:** __

## Estructura
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
