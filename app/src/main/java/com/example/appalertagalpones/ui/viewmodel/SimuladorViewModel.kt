package com.example.appalertagalpones.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appalertagalpones.data.MonitoreoRepository
import com.example.appalertagalpones.data.PreferenciasRepository
import com.example.appalertagalpones.data.model.EvaluadorEstado
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.Umbrales
import com.example.appalertagalpones.data.model.puedeVer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SimuladorErrores(
    val galpon: String? = null,
    val temperatura: String? = null,
    val humedad: String? = null
)

data class SimuladorForm(
    val galponId: Int? = null,
    val temperatura: String = "",
    val humedad: String = "",
    val errores: SimuladorErrores = SimuladorErrores(),
    val aplicando: Boolean = false,
    val mensaje: String? = null
)

data class SimuladorUiState(
    val galpones: List<Galpon> = emptyList(),
    val umbrales: Umbrales = Umbrales(),
    val form: SimuladorForm = SimuladorForm()
)

class SimuladorViewModel(
    private val repositorio: MonitoreoRepository,
    private val preferencias: PreferenciasRepository
) : ViewModel() {

    private val form = MutableStateFlow(SimuladorForm())

    val uiState: StateFlow<SimuladorUiState> = combine(
        repositorio.galpones, preferencias.usuario, preferencias.umbrales, form
    ) { galpones, usuario, umbrales, f ->
        SimuladorUiState(galpones.filter { usuario?.puedeVer(it) == true }, umbrales, f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SimuladorUiState())

    fun seleccionarGalpon(g: Galpon) = form.update {
        it.copy(
            galponId = g.id,
            temperatura = g.temperatura.toString(),
            humedad = g.humedad.toString(),
            errores = SimuladorErrores(),
            mensaje = null
        )
    }

    fun onTemperatura(v: String) = form.update {
        it.copy(temperatura = v, errores = it.errores.copy(temperatura = Validadores.temperatura(v)), mensaje = null)
    }

    fun onHumedad(v: String) = form.update {
        it.copy(humedad = v, errores = it.errores.copy(humedad = Validadores.humedad(v)), mensaje = null)
    }

    fun validarFormulario(): Boolean {
        val f = form.value
        val errores = SimuladorErrores(
            galpon = if (f.galponId == null) "Selecciona un galpón" else null,
            temperatura = Validadores.temperatura(f.temperatura),
            humedad = Validadores.humedad(f.humedad)
        )
        form.update { it.copy(errores = errores) }
        return errores.galpon == null && errores.temperatura == null && errores.humedad == null
    }

    fun aplicar() {
        if (!validarFormulario()) return
        val f = form.value
        val id = f.galponId ?: return
        val t = f.temperatura.aDouble() ?: return
        val h = f.humedad.aDouble() ?: return
        ejecutar(id, t, h)
    }

    /** Atajo para demostrar una alerta crítica: temperatura sobre el umbral crítico configurado. */
    fun forzarCritica() {
        val f = form.value
        val id = f.galponId
        if (id == null) {
            form.update { it.copy(errores = it.errores.copy(galpon = "Selecciona un galpón")) }
            return
        }
        viewModelScope.launch {
            val u = preferencias.umbrales.first()
            val t = u.tempCritico + 1.5
            val h = f.humedad.aDouble() ?: 60.0
            form.update { it.copy(temperatura = t.toString(), humedad = h.toString(), errores = SimuladorErrores()) }
            ejecutar(id, t, h)
        }
    }

    fun restablecer() {
        val id = form.value.galponId
        if (id == null) {
            form.update { it.copy(errores = it.errores.copy(galpon = "Selecciona un galpón")) }
            return
        }
        viewModelScope.launch {
            form.update { it.copy(aplicando = true) }
            repositorio.restablecerSimulacion(id)
            form.update { it.copy(aplicando = false, mensaje = "Simulación restablecida: el galpón vuelve a los valores simulados automáticos.") }
        }
    }

    private fun ejecutar(id: Int, t: Double, h: Double) {
        viewModelScope.launch {
            form.update { it.copy(aplicando = true, mensaje = null) }
            val umbrales = preferencias.umbrales.first()
            repositorio.simular(id, t, h)
            val evaluacion = EvaluadorEstado.evaluar(t, h, umbrales)
            form.update {
                it.copy(aplicando = false, mensaje = "Lectura aplicada. Estado resultante: ${evaluacion.estado.etiqueta}. ${evaluacion.motivo}.")
            }
        }
    }
}
