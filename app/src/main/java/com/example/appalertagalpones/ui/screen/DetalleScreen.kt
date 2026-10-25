@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.alertagalpones.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.alertagalpones.data.model.AccionRegistrada
import com.example.alertagalpones.data.model.Estado
import com.example.alertagalpones.data.model.Rol
import com.example.alertagalpones.data.model.TipoAccion
import com.example.alertagalpones.data.model.Umbrales
import com.example.alertagalpones.data.model.Usuario
import com.example.alertagalpones.data.model.toEstado
import com.example.alertagalpones.ui.components.GraficoLinea
import com.example.alertagalpones.ui.theme.color
import com.example.alertagalpones.ui.theme.icono
import com.example.alertagalpones.ui.viewmodel.DetalleUiState
import com.example.alertagalpones.ui.viewmodel.DetalleViewModel
import com.example.alertagalpones.ui.viewmodel.PeriodoGrafico
import com.example.alertagalpones.util.formatoFechaHora
import com.example.alertagalpones.util.grados
import com.example.alertagalpones.util.porcentaje
import com.example.appalertagalpones.data.model.AccionRegistrada
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.Rol
import com.example.appalertagalpones.data.model.TipoAccion
import com.example.appalertagalpones.data.model.Umbrales
import com.example.appalertagalpones.data.model.Usuario
import com.example.appalertagalpones.data.model.toEstado
import com.example.appalertagalpones.ui.components.GraficoLinea
import com.example.appalertagalpones.ui.theme.color
import com.example.appalertagalpones.ui.theme.icono
import com.example.appalertagalpones.ui.viewmodel.DetalleUiState
import com.example.appalertagalpones.ui.viewmodel.DetalleViewModel
import com.example.appalertagalpones.ui.viewmodel.PeriodoGrafico
import java.io.File

@Composable
fun DetalleScreen(viewModel: DetalleViewModel, onRegistrarAccion: (Int) -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val galpon = estado.galpon
    if (galpon == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val contexto = LocalContext.current
    val estadoGalpon = galpon.estado.toEstado()
    val fondo by animateColorAsState(estadoGalpon.color().copy(alpha = 0.14f), tween(500), label = "fondo")
    val umbrales = Umbrales() // líneas de referencia; los umbrales reales se editan en "Umbrales"

    val cabecera: @Composable () -> Unit = {
        CabeceraGalpon(estado, fondo, estadoGalpon)
        Spacer(Modifier.height(12.dp))
        ResponsablesCard(estado.operario, estado.supervisor) { telefono ->
            contexto.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono.filter { it.isDigit() || it == '+' })))
        }
    }
    val graficos: @Composable () -> Unit = {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PeriodoGrafico.entries.forEach { p ->
                        FilterChip(
                            selected = estado.periodo == p,
                            onClick = { viewModel.cambiarPeriodo(p) },
                            label = { Text(p.etiqueta) }
                        )
                    }
                }
                GraficoLinea(
                    titulo = "Temperatura (°C)", unidad = "°C",
                    valores = estado.mediciones.map { it.temperatura },
                    color = MaterialTheme.colorScheme.primary,
                    umbralAdvertencia = umbrales.tempAdvertencia, umbralCritico = umbrales.tempCritico
                )
                GraficoLinea(
                    titulo = "Humedad relativa (%)", unidad = "%",
                    valores = estado.mediciones.map { it.humedad },
                    color = MaterialTheme.colorScheme.secondary,
                    umbralAdvertencia = umbrales.humAdvertencia, umbralCritico = umbrales.humCritico
                )
                Text("Líneas punteadas: umbral de advertencia (naranja) y crítico (rojo).", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Diseño adaptable (Guía 9): en pantallas anchas se muestran lado a lado.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth >= 600.dp) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f)) { cabecera() }
                    Column(Modifier.weight(1f)) { graficos() }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column { cabecera() }
                    graficos()
                }
            }
        }

        if (estado.usuario?.rol != Rol.JEFATURA) {
            Button(onClick = { onRegistrarAccion(galpon.id) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar acción realizada")
            }
        }

        Text("Acciones registradas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (estado.acciones.isEmpty()) {
            Text("Aún no hay acciones para este galpón.", style = MaterialTheme.typography.bodySmall)
        }
        estado.acciones.take(10).forEach { AccionItem(it) }

        Text("Historial de mediciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        estado.mediciones.takeLast(15).reversed().forEach { m ->
            val e = m.estado.toEstado()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatoFechaHora(m.fechaMs), style = MaterialTheme.typography.bodySmall)
                Text("${m.temperatura.grados()} · ${m.humedad.porcentaje()}", style = MaterialTheme.typography.bodySmall)
                Text(e.etiqueta, color = e.color(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CabeceraGalpon(estado: DetalleUiState, fondo: androidx.compose.ui.graphics.Color, estadoGalpon: Estado) {
    val g = estado.galpon ?: return
    Card(colors = CardDefaults.cardColors(containerColor = fondo), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(g.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("${g.fundo} · ${g.sector} · Pollos de engorda · ${g.edadDias} días", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Thermostat, null, Modifier.size(32.dp))
                Text(g.temperatura.grados(), fontSize = 38.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WaterDrop, null, Modifier.size(20.dp))
                Text(" Humedad ${g.humedad.porcentaje()}")
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(estadoGalpon.icono(), null, tint = estadoGalpon.color())
                Text(" ${estadoGalpon.etiqueta}", color = estadoGalpon.color(), fontWeight = FontWeight.Bold)
            }
            Text("Medido: ${formatoFechaHora(g.actualizadoMs)}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text(estadoGalpon.recomendacion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ResponsablesCard(operario: Usuario?, supervisor: Usuario?, onLlamar: (String) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Responsables (datos ficticios)", fontWeight = FontWeight.Bold)
            operario?.let { Text("Operario: ${it.nombre}") }
            supervisor?.let {
                Text("Supervisor: ${it.nombre} · ${it.telefono}")
                OutlinedButton(onClick = { onLlamar(it.telefono) }) {
                    Icon(Icons.Default.Call, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Llamar al supervisor")
                }
            }
        }
    }
}

@Composable
private fun AccionItem(a: AccionRegistrada) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (a.fotoPath != null) {
                AsyncImage(
                    model = File(a.fotoPath),
                    contentDescription = "Evidencia",
                    modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
            }
            Column {
                Text(TipoAccion.valueOf(a.tipo).etiqueta, fontWeight = FontWeight.Bold)
                Text(a.descripcion)
                Text("${a.usuarioNombre} · ${formatoFechaHora(a.fechaMs)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
