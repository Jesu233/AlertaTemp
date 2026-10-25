package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertagalpones.data.PreferenciasRepository
import com.example.alertagalpones.data.model.Umbrales
import com.example.appalertagalpones.data.PreferenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AjustesErrores(
    val tempAdvertencia: String? = null,
    val tempCritico: String? = null,
    val humAdvertencia: String? = null,
    val humCritico: String? = null
)

data class AjustesUiState(
    val tempAdvertencia: String = "",
    val tempCritico: String = "",
    val humAdvertencia: String = "",
    val humCritico: String = "",
    val errores: AjustesErrores = AjustesErrores(),
    val guardado: Boolean = false
)

/** Pregunta 1: umbrales ficticios y configurables. */
class AjustesViewModel(private val preferencias: PreferenciasRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesUiState())
    val uiState: StateFlow<AjustesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val u = preferencias.umbrales.first()
            _uiState.update {
                it.copy(
                    tempAdvertencia = u.tempAdvertencia.toString(), tempCritico = u.tempCritico.toString(),
                    humAdvertencia = u.humAdvertencia.toString(), humCritico = u.humCritico.toString()
                )
            }
        }
    }

    fun onTempAdvertencia(v: String) = _uiState.update { it.copy(tempAdvertencia = v, errores = it.errores.copy(tempAdvertencia = Validadores.temperatura(v)), guardado = false) }
    fun onTempCritico(v: String) = _uiState.update { it.copy(tempCritico = v, errores = it.errores.copy(tempCritico = Validadores.temperatura(v)), guardado = false) }
    fun onHumAdvertencia(v: String) = _uiState.update { it.copy(humAdvertencia = v, errores = it.errores.copy(humAdvertencia = Validadores.humedad(v)), guardado = false) }
    fun onHumCritico(v: String) = _uiState.update { it.copy(humCritico = v, errores = it.errores.copy(humCritico = Validadores.humedad(v)), guardado = false) }

    fun validarFormulario(): Boolean {
        val s = _uiState.value
        var errores = AjustesErrores(
            tempAdvertencia = Validadores.temperatura(s.tempAdvertencia),
            tempCritico = Validadores.temperatura(s.tempCritico),
            humAdvertencia = Validadores.humedad(s.humAdvertencia),
            humCritico = Validadores.humedad(s.humCritico)
        )
        val ta = s.tempAdvertencia.aDouble()
        val tc = s.tempCritico.aDouble()
        val ha = s.humAdvertencia.aDouble()
        val hc = s.humCritico.aDouble()
        if (errores.tempCritico == null && errores.tempAdvertencia == null && ta != null && tc != null) {
            errores = errores.copy(tempCritico = Validadores.coherencia(ta, tc, "temperatura"))
        }
        if (errores.humCritico == null && errores.humAdvertencia == null && ha != null && hc != null) {
            errores = errores.copy(humCritico = Validadores.coherencia(ha, hc, "humedad"))
        }
        _uiState.update { it.copy(errores = errores) }
        return errores == AjustesErrores()
    }

    fun guardar() {
        if (!validarFormulario()) return
        val s = _uiState.value
        val u = Umbrales(
            tempAdvertencia = s.tempAdvertencia.aDouble() ?: return,
            tempCritico = s.tempCritico.aDouble() ?: return,
            humAdvertencia = s.humAdvertencia.aDouble() ?: return,
            humCritico = s.humCritico.aDouble() ?: return
        )
        viewModelScope.launch {
            preferencias.guardarUmbrales(u)
            _uiState.update { it.copy(guardado = true) }
        }
    }
}
