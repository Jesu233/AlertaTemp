@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appalertagalpones.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.appalertagalpones.data.model.TipoAccion
import com.example.appalertagalpones.ui.components.CampoTexto
import com.example.appalertagalpones.ui.components.SelectorDesplegable
import com.example.appalertagalpones.ui.viewmodel.AccionViewModel


@Composable
fun AccionScreen(viewModel: AccionViewModel, onGuardado: () -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    // Recursos nativos (Guía 13): cámara y galería mediante contratos del sistema.
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) viewModel.imagenDesdeCamara(bitmap)
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.imagenDesdeGaleria(uri)
    }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            Toast.makeText(contexto, "Acción registrada", Toast.LENGTH_SHORT).show()
            onGuardado()
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(estado.galponNombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Registrado por: ${estado.usuarioNombre} (acción simulada, no controla equipos reales)", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(4.dp))

        SelectorDesplegable(
            etiqueta = "Tipo",
            seleccion = estado.tipo?.etiqueta ?: "Selecciona",
            opciones = TipoAccion.entries.map { it.etiqueta to it },
            onSeleccion = viewModel::onTipo,
            error = estado.errores.tipo
        )
        CampoTexto(
            valor = estado.descripcion,
            onCambio = viewModel::onDescripcion,
            etiqueta = "Descripción de la acción",
            icono = Icons.Default.Create,
            error = estado.errores.descripcion,
            lineas = 3
        )

        Text("Evidencia (opcional)", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { camara.launch(null) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.CameraAlt, null)
                Spacer(Modifier.width(6.dp))
                Text("Cámara")
            }
            OutlinedButton(onClick = { galeria.launch("image/*") }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.PhotoLibrary, null)
                Spacer(Modifier.width(6.dp))
                Text("Galería")
            }
        }
        val imagen = estado.imagen
        AnimatedVisibility(imagen.origen.isNotEmpty(), enter = fadeIn() + scaleIn()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when {
                    imagen.bitmap != null -> Image(
                        bitmap = imagen.bitmap.asImageBitmap(),
                        contentDescription = "Fotografía tomada",
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        contentScale = ContentScale.Fit
                    )
                    imagen.uri != null -> AsyncImage(
                        model = imagen.uri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Text("Imagen obtenida desde: ${imagen.origen}", style = MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick = viewModel::limpiarImagen) { Text("Limpiar imagen") }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = viewModel::guardar, enabled = !estado.guardando, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            if (estado.guardando) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Guardar acción")
            }
        }
    }
}
