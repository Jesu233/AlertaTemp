package com.example.appalertagalpones.ui.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appalertagalpones.util.formatoFechaHora
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.TipoAccion
import com.example.appalertagalpones.ui.theme.color
import com.example.appalertagalpones.ui.viewmodel.PanelViewModel

@Composable
fun PanelScreen(viewModel: PanelViewModel) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Resumen general", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            // Adaptable: en pantallas pequeñas se apilan en dos filas.
            val compacto = maxWidth < 600.dp
            if (compacto) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Dato("Galpones", estado.totalGalpones, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                        Dato("Pendientes", estado.alertasPendientes, Estado.CRITICO.color(), Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Dato("Críticos", estado.criticos, Estado.CRITICO.color(), Modifier.weight(1f))
                        Dato("Advertencia", estado.advertencias, Estado.ADVERTENCIA.color(), Modifier.weight(1f))
                        Dato("Normales", estado.normales, Estado.NORMAL.color(), Modifier.weight(1f))
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Dato("Galpones", estado.totalGalpones, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    Dato("Pendientes", estado.alertasPendientes, Estado.CRITICO.color(), Modifier.weight(1f))
                    Dato("Críticos", estado.criticos, Estado.CRITICO.color(), Modifier.weight(1f))
                    Dato("Advertencia", estado.advertencias, Estado.ADVERTENCIA.color(), Modifier.weight(1f))
                    Dato("Normales", estado.normales, Estado.NORMAL.color(), Modifier.weight(1f))
                }
            }
        }

        Text("Por fundo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        estado.fundos.forEach { f ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(f.fundo, fontWeight = FontWeight.Bold)
                    Text("${f.total} galpones · ${f.criticos} críticos · ${f.advertencias} en advertencia · ${f.normales} normales")
                }
            }
        }

        Text("Últimas acciones registradas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (estado.ultimasAcciones.isEmpty()) Text("Aún no hay acciones registradas.", style = MaterialTheme.typography.bodySmall)
        estado.ultimasAcciones.forEach { a ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${a.galponNombre} · ${TipoAccion.valueOf(a.tipo).etiqueta}", fontWeight = FontWeight.Bold)
                    Text(a.descripcion)
                    Text("${a.usuarioNombre} · ${formatoFechaHora(a.fechaMs)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: Int, color: Color, modifier: Modifier) {
    val n by animateIntAsState(valor, tween(600), label = "dato")
    Card(modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$n", style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall)
        }
    }
}
