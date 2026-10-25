package com.example.appalertagalpones.ui.navigation

object AppRoutes {
    const val LOGIN = "login"
    const val GALPONES = "galpones"
    const val DETALLE = "detalle"
    const val ACCION = "accion"
    const val ALERTAS = "alertas"
    const val PANEL = "panel"
    const val SIMULADOR = "simulador"
    const val CONTACTOS = "contactos"
    const val AJUSTES = "ajustes"
    const val PERFIL = "perfil"

    /** Destinos de primer nivel: muestran el ícono de menú; el resto muestra "volver". */
    val principales = listOf(GALPONES, ALERTAS, PANEL, SIMULADOR, CONTACTOS, AJUSTES, PERFIL)

    fun titulo(ruta: String?): String = when (ruta) {
        GALPONES -> "Galpones"
        DETALLE -> "Detalle del galpón"
        ACCION -> "Registrar acción"
        ALERTAS -> "Alertas y eventos"
        PANEL -> "Panel general"
        SIMULADOR -> "Simulador de lecturas"
        CONTACTOS -> "Contactos"
        AJUSTES -> "Umbrales"
        PERFIL -> "Perfil"
        else -> "Alerta Galpones"
    }
}
