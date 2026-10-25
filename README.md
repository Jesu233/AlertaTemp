# App AlertaTemp – DSY1105 (Evaluación Parcial 2)

App Android (Kotlin + Jetpack Compose, MVVM) del caso **"Alerta temprana por altas temperaturas"**
(empresa avícola y ganadera, pollos de engorda). Operarios, supervisores y jefaturas consultan desde el celular
la temperatura y humedad de cada galpón, reciben notificaciones críticas, revisan el historial y registran acciones.

**Integrantes:** _Yuler Campos_ · _María Jesús Silva_

## Estructura
```
com.example.appalertagalpones
├── MainActivity.kt              Crea BD, repositorios y fábrica de ViewModels
├── data
│   ├── model                    Entidades Room y modelos: Galpon, Medicion, Alerta, AccionRegistrada, Usuario, Estado
│   ├── remote                   MonitoreoApi + FakeMonitoreoApi
│   ├── MonitoreoDao / AppDatabase / MonitoreoRepository     Room
│   ├── PreferenciasRepository   DataStore: sesión y umbrales
│   └── FotoStorage              Evidencia en almacenamiento interno
├── ui
│   ├── navigation               AppRoutes + AppNavigation: Scaffold, TopAppBar, NavigationBar, Drawer
│   ├── screen                   Pantallas Compose
│   ├── viewmodel                ViewModels con StateFlow, UiState y validarFormulario(), fábrica
│   ├── components               CampoTexto, SelectorDesplegable, GraficoLinea
│   └── theme
└── util                         Notificador, formatos
```
