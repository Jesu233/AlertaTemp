package com.example.appalertagalpones.data.model

enum class Rol(val etiqueta: String) {
    OPERARIO("Operario"), SUPERVISOR("Supervisor"), JEFATURA("Jefatura")
}


data class Usuario(
    val usuario: String,
    val clave: String,
    val nombre: String,
    val rol: Rol,
    val telefono: String,
    val correo: String
)


fun Usuario.puedeVer(g: Galpon): Boolean = when (rol) {
    Rol.OPERARIO -> g.operarioId == usuario
    Rol.SUPERVISOR -> g.supervisorId == usuario
    Rol.JEFATURA -> true
}

object UsuariosDemo {
    const val CLAVE = "Galpon123"

    val todos = listOf(
        Usuario("operario1", CLAVE, "Carlos Rojas", Rol.OPERARIO, "+56 9 5555 0101", "carlos.rojas@ejemplo.cl"),
        Usuario("operario2", CLAVE, "Marta Soto", Rol.OPERARIO, "+56 9 5555 0102", "marta.soto@ejemplo.cl"),
        Usuario("operario3", CLAVE, "Luis Pardo", Rol.OPERARIO, "+56 9 5555 0103", "luis.pardo@ejemplo.cl"),
        Usuario("supervisor1", CLAVE, "Andrea Fuentes", Rol.SUPERVISOR, "+56 9 5555 0201", "andrea.fuentes@ejemplo.cl"),
        Usuario("supervisor2", CLAVE, "Ricardo Núñez", Rol.SUPERVISOR, "+56 9 5555 0202", "ricardo.nunez@ejemplo.cl"),
        Usuario("jefatura1", CLAVE, "Patricia Vega", Rol.JEFATURA, "+56 9 5555 0301", "patricia.vega@ejemplo.cl")
    )

    fun buscar(id: String?): Usuario? = todos.firstOrNull { it.usuario == id }

    fun autenticar(usuario: String, clave: String): Usuario? =
        todos.firstOrNull { it.usuario == usuario.trim() && it.clave == clave }

    fun supervisores(): List<Usuario> = todos.filter { it.rol == Rol.SUPERVISOR }
}