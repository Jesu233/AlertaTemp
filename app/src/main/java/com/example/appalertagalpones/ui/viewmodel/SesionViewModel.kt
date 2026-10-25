package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertagalpones.data.PreferenciasRepository
import com.example.alertagalpones.data.model.Usuario
import com.example.alertagalpones.data.model.UsuariosDemo
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.Usuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginErrores(val usuario: String? = null, val clave: String? = null)

data class LoginUiState(
    val usuario: String = "",
    val clave: String = "",
    val recordar: Boolean = true,
    val errores: LoginErrores = LoginErrores(),
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

class SesionViewModel(private val preferencias: PreferenciasRepository) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        viewModelScope.launch {
            // Si no se marcó "mantener sesión", la sesión anterior se descarta al abrir la app.
            if (!preferencias.recordarSesion.first()) preferencias.cerrarSesion()
            _usuario.value = preferencias.usuario.first()
            _cargando.value = false
            preferencias.usuario.collect { _usuario.value = it }
        }
    }

    fun onUsuario(v: String) = _login.update {
        it.copy(usuario = v, errores = it.errores.copy(usuario = Validadores.usuario(v)), errorGeneral = null)
    }

    fun onClave(v: String) = _login.update {
        it.copy(clave = v, errores = it.errores.copy(clave = Validadores.clave(v)), errorGeneral = null)
    }

    fun onRecordar(v: Boolean) = _login.update { it.copy(recordar = v) }

    fun validarFormulario(): Boolean {
        val s = _login.value
        val errores = LoginErrores(Validadores.usuario(s.usuario), Validadores.clave(s.clave))
        _login.update { it.copy(errores = errores) }
        return errores.usuario == null && errores.clave == null
    }

    fun ingresar() {
        if (!validarFormulario()) return
        val s = _login.value
        viewModelScope.launch {
            _login.update { it.copy(cargando = true, errorGeneral = null) }
            delay(600) // simula la autenticación
            val u = UsuariosDemo.autenticar(s.usuario, s.clave)
            if (u == null) {
                _login.update { it.copy(cargando = false, errorGeneral = "Usuario o contraseña incorrectos") }
            } else {
                preferencias.iniciarSesion(u.usuario, s.recordar)
                _login.value = LoginUiState(recordar = s.recordar)
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch { preferencias.cerrarSesion() }
    }
}
