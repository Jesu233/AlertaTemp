package com.example.appalertagalpones.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertagalpones.data.FotoStorage
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.TipoAccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Igual que la Guía 13: imagen desde cámara (Bitmap) o galería (Uri). */
data class ImagenUiState(
    val bitmap: Bitmap? = null,
    val uri: Uri? = null,
    val origen: String = ""
)

data class AccionErrores(val tipo: String? = null, val descripcion: String? = null)

data class AccionUiState(
    val galponNombre: String = "",
    val usuarioNombre: String = "",
    val tipo: TipoAccion? = null,
    val descripcion: String = "",
    val imagen: ImagenUiState = ImagenUiState(),
    val errores: AccionErrores = AccionErrores(),
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

class AccionViewModel(
    private val repositorio: MonitoreoRepository,
    private val preferencias: PreferenciasRepository,
    private val fotos: FotoStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccionUiState())
    val uiState: StateFlow<AccionUiState> = _uiState.asStateFlow()

    private var galponId: Int = -1
    private var galponNombre: String = ""
    private var usuarioId: String = ""
    private var usuarioNombre: String = ""

    /** Se llama desde la navegación antes de abrir el formulario (sin pasar argumentos por la ruta, Guía 11). */
    fun prepararFormulario(id: Int) {
        galponId = id
        _uiState.value = AccionUiState() // reinicio inmediato
        viewModelScope.launch {
            val galpon = repositorio.obtenerGalpon(id)
            val usuario = preferencias.usuario.first()
            galponNombre = galpon?.nombre ?: ""
            usuarioId = usuario?.usuario ?: ""
            usuarioNombre = usuario?.nombre ?: ""
            _uiState.update { it.copy(galponNombre = galponNombre, usuarioNombre = usuarioNombre) }
        }
    }

    fun onTipo(t: TipoAccion) = _uiState.update { it.copy(tipo = t, errores = it.errores.copy(tipo = null)) }

    fun onDescripcion(v: String) = _uiState.update {
        it.copy(descripcion = v, errores = it.errores.copy(descripcion = Validadores.descripcion(v)))
    }

    fun imagenDesdeCamara(bitmap: Bitmap?) = _uiState.update {
        it.copy(imagen = ImagenUiState(bitmap = bitmap, origen = if (bitmap != null) "Cámara" else ""))
    }

    fun imagenDesdeGaleria(uri: Uri?) = _uiState.update {
        it.copy(imagen = ImagenUiState(uri = uri, origen = if (uri != null) "Galería" else ""))
    }

    fun limpiarImagen() = _uiState.update { it.copy(imagen = ImagenUiState()) }

    fun validarFormulario(): Boolean {
        val s = _uiState.value
        val errores = AccionErrores(
            tipo = if (s.tipo == null) "Selecciona el tipo de acción" else null,
            descripcion = Validadores.descripcion(s.descripcion)
        )
        _uiState.update { it.copy(errores = errores) }
        return errores.tipo == null && errores.descripcion == null
    }

    fun guardar() {
        if (!validarFormulario()) return
        val s = _uiState.value
        val tipo = s.tipo ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }
            val ruta = withContext(Dispatchers.IO) {
                when {
                    s.imagen.bitmap != null -> fotos.guardarBitmap(s.imagen.bitmap)
                    s.imagen.uri != null -> fotos.guardarDesdeUri(s.imagen.uri)
                    else -> null
                }
            }
            repositorio.registrarAccion(
                AccionRegistrada(
                    usuarioId = usuarioId,
                    usuarioNombre = usuarioNombre,
                    galponId = galponId,
                    galponNombre = galponNombre,
                    tipo = tipo.name,
                    descripcion = s.descripcion.trim(),
                    fotoPath = ruta,
                    fechaMs = System.currentTimeMillis()
                )
            )
            _uiState.update { it.copy(guardando = false, guardado = true) }
        }
    }
}
