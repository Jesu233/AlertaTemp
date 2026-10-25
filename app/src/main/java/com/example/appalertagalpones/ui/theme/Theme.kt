package com.example.appalertagalpones.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appalertagalpones.data.model.AtencionAlerta
import com.example.appalertagalpones.data.model.Estado

private val Esquema = lightColorScheme(
    primary = Color(0xFF2E7D32),
    secondary = Color(0xFF1565C0),
    tertiary = Color(0xFFEF8F00),
    background = Color(0xFFF6F8F5)
)

@Composable
fun AlertaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, content = content)
}

/** Indicadores visuales del estado (pregunta 3). */
fun Estado.color(): Color = when (this) {
    Estado.NORMAL -> Color(0xFF2E7D32)
    Estado.ADVERTENCIA -> Color(0xFFEF8F00)
    Estado.CRITICO -> Color(0xFFC62828)
}

fun Estado.icono(): ImageVector = when (this) {
    Estado.NORMAL -> Icons.Default.CheckCircle
    Estado.ADVERTENCIA -> Icons.Default.Info
    Estado.CRITICO -> Icons.Default.Warning
}

fun AtencionAlerta.color(): Color = when (this) {
    AtencionAlerta.PENDIENTE -> Color(0xFFC62828)
    AtencionAlerta.EN_ATENCION -> Color(0xFFEF8F00)
    AtencionAlerta.ATENDIDA -> Color(0xFF2E7D32)
}
