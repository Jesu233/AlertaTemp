package com.example.alertagalpones.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alertagalpones.util.formatoFechaHora
import com.example.alertagalpones.util.grados
import com.example.alertagalpones.util.porcentaje
import com.example.appalertagalpones.data.model.Estado
import com.example.appalertagalpones.data.model.Galpon
import com.example.appalertagalpones.data.model.toEstado
import com.example.appalertagalpones.ui.theme.color
import com.example.appalertagalpones.ui.theme.icono
import com.example.appalertagalpones.ui.viewmodel.GalponesViewModel

@Composable
fun GalponesScreen(viewModel: GalponesViewModel, onGalponClick: (Int) -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    // Recurso nativo: permiso de notificaciones (Android 13+)
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) permiso.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    val giro by rememberInfiniteTransition(label = "giro")
        .animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "giro")

    // Diseño adaptable (Guía 9): 1 columna en pantallas pequeñas, 2 desde 600 dp.
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val columnas = if (maxWidth < 600.dp) 1 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnas),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Hola, ${estado.usuario?.nombre ?: ""}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            estado.ultimaActualizacionMs?.let { "Última medición: ${formatoFechaHora(it)}" } ?: "Sin mediciones aún",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = viewModel::refrescar, enabled = !estado.actualizando) {
                        Icon(Icons.Default.Refresh, "Actualizar", Modifier.rotate(if (estado.actualizando) giro else 0f))
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResumenCard(Estado.CRITICO, estado.criticos, estado.filtro == Estado.CRITICO, { viewModel.filtrar(Estado.CRITICO) }, Modifier.weight(1f))
                    ResumenCard(Estado.ADVERTENCIA, estado.advertencias, estado.filtro == Estado.ADVERTENCIA, { viewModel.filtrar(Estado.ADVERTENCIA) }, Modifier.weight(1f))
                    ResumenCard(Estado.NORMAL, estado.normales, estado.filtro == Estado.NORMAL, { viewModel.filtrar(Estado.NORMAL) }, Modifier.weight(1f))
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                AnimatedVisibility(estado.sinConexion) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFEF8F00))
                            Spacer(Modifier.width(8.dp))
                            Text("Sin conexión. Se muestran los últimos datos guardados en el dispositivo.")
                        }
                    }
                }
            }
            if (estado.galpones.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("No hay galpones para mostrar. Presiona actualizar o quita el filtro.")
                }
            }
            items(estado.galpones, key = { it.id }) { galpon ->
                GalponCard(galpon) { onGalponClick(galpon.id) }
            }
        }
    }
}

@Composable
private fun ResumenCard(estado: Estado, cantidad: Int, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val n by animateIntAsState(cantidad, tween(600), label = "contador")
    val borde by animateColorAsState(if (seleccionado) estado.color() else Color.Transparent, label = "borde")
    Card(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, borde),
        colors = CardDefaults.cardColors(containerColor = estado.color().copy(alpha = 0.12f))
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$n", style = MaterialTheme.typography.headlineMedium, color = estado.color(), fontWeight = FontWeight.Bold)
            Text(estado.etiqueta, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun GalponCard(galpon: Galpon, onClick: () -> Unit) {
    val estado = galpon.estado.toEstado()
    val fondo by animateColorAsState(estado.color().copy(alpha = 0.12f), tween(500), label = "fondo")
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = fondo),
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(estado.color(), CircleShape), contentAlignment = Alignment.Center) {
                Icon(estado.icono(), null, tint = Color.White)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(galpon.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${galpon.fundo} · ${galpon.sector} · ${galpon.edadDias} días", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Thermostat, null, Modifier.size(16.dp))
                    Text(" ${galpon.temperatura.grados()}   ")
                    Icon(Icons.Default.WaterDrop, null, Modifier.size(16.dp))
                    Text(" ${galpon.humedad.porcentaje()}")
                }
                Text(formatoFechaHora(galpon.actualizadoMs), style = MaterialTheme.typography.bodySmall)
            }
            Text(estado.etiqueta, color = estado.color(), fontWeight = FontWeight.Bold)
        }
    }
}
