@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appalertagalpones.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appalertagalpones.util.formatoFechaHora
import com.example.appalertagalpones.util.grados
import com.example.appalertagalpones.util.porcentaje
import com.example.appalertagalpones.data.model.Alerta
import com.example.appalertagalpones.data.model.AtencionAlerta
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.Rol
import com.example.appalertagalpones.data.model.toEstado
import com.example.appalertagalpones.ui.components.SelectorDesplegable
import com.example.appalertagalpones.ui.theme.color
import com.example.appalertagalpones.ui.theme.icono
import com.example.appalertagalpones.ui.viewmodel.AlertasViewModel
import com.example.appalertagalpones.ui.viewmodel.FiltroFecha

@Composable
fun AlertasScreen(viewModel: AlertasViewModel) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val puedeGestionar = estado.usuario?.rol != Rol.JEFATURA

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pendientes de atención: ${estado.pendientes}", fontWeight = FontWeight.Bold)
                Text("Tipo", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(estado.filtros.tipo == null, { viewModel.filtrarTipo(null) }, { Text("Todas") })
                    FilterChip(estado.filtros.tipo == Estado.CRITICO, { viewModel.filtrarTipo(Estado.CRITICO) }, { Text("Críticas") })
                    FilterChip(estado.filtros.tipo == Estado.ADVERTENCIA, { viewModel.filtrarTipo(Estado.ADVERTENCIA) }, { Text("Advertencias") })
                }
                Text("Fecha", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FiltroFecha.entries.forEach { f ->
                        FilterChip(estado.filtros.fecha == f, { viewModel.filtrarFecha(f) }, { Text(f.etiqueta) })
                    }
                }
                SelectorDesplegable(
                    etiqueta = "Galpón",
                    seleccion = estado.galpones.firstOrNull { it.id == estado.filtros.galponId }?.nombre ?: "Todos",
                    opciones = listOf<Pair<String, Int?>>("Todos" to null) + estado.galpones.map { it.nombre to it.id },
                    onSeleccion = { viewModel.filtrarGalpon(it) }
                )
            }
        }
        if (estado.alertas.isEmpty()) {
            item { Text("No hay alertas que coincidan con los filtros.") }
        }
        items(estado.alertas, key = { it.id }) { alerta ->
            AlertaItem(
                alerta = alerta,
                puedeGestionar = puedeGestionar,
                onTomar = { viewModel.tomarAlerta(alerta.id) },
                onAtender = { viewModel.marcarAtendida(alerta.id) }
            )
        }
    }
}

@Composable
private fun AlertaItem(alerta: Alerta, puedeGestionar: Boolean, onTomar: () -> Unit, onAtender: () -> Unit) {
    val tipo = alerta.tipo.toEstado()
    val atencion = AtencionAlerta.valueOf(alerta.atencion)
    val fondo by animateColorAsState(
        if (atencion == AtencionAlerta.ATENDIDA) MaterialTheme.colorScheme.surfaceVariant else tipo.color().copy(alpha = 0.12f),
        label = "fondo"
    )
    Card(colors = CardDefaults.cardColors(containerColor = fondo), modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(tipo.icono(), null, tint = tipo.color())
                Spacer(Modifier.width(8.dp))
                Text(alerta.galponNombre, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(tipo.etiqueta, color = tipo.color(), fontWeight = FontWeight.Bold)
            }
            Text(alerta.mensaje, Modifier.padding(top = 4.dp))
            Text(
                "Medición: ${alerta.temperatura.grados()} · ${alerta.humedad.porcentaje()}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(formatoFechaHora(alerta.fechaMs), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Atención: ${atencion.etiqueta}", color = atencion.color(), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (puedeGestionar) {
                    Crossfade(atencion, label = "atencion") { a ->
                        when (a) {
                            AtencionAlerta.PENDIENTE -> FilledTonalButton(onClick = onTomar) { Text("Tomar alerta") }
                            AtencionAlerta.EN_ATENCION -> OutlinedButton(onClick = onAtender) { Text("Marcar atendida") }
                            AtencionAlerta.ATENDIDA -> Text("Cerrada")
                        }
                    }
                }
            }
        }
    }
}
