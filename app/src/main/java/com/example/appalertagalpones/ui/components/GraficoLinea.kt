package com.example.appalertagalpones.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GraficoLinea(
    titulo: String,
    unidad: String,
    valores: List<Double>,
    color: Color,
    umbralAdvertencia: Double?,
    umbralCritico: Double?,
    modifier: Modifier = Modifier
) {
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(valores.size) {
        progreso.snapTo(0f)
        progreso.animateTo(1f, tween(800))
    }
    Column(modifier) {
        Text(titulo, fontWeight = FontWeight.Bold)
        if (valores.size < 2) {
            Text("Aún no hay suficientes mediciones en este período.", style = MaterialTheme.typography.bodySmall)
            return@Column
        }
        Text(
            "mín ${"%.1f".format(valores.min())} · máx ${"%.1f".format(valores.max())} $unidad",
            style = MaterialTheme.typography.bodySmall
        )
        Canvas(Modifier.fillMaxWidth().height(140.dp).padding(top = 8.dp)) {
            val limites = listOfNotNull(umbralAdvertencia, umbralCritico) + valores
            val minimo = limites.min() - 1
            val maximo = limites.max() + 1
            fun y(v: Double) = size.height - ((v - minimo) / (maximo - minimo)).toFloat() * size.height
            val paso = size.width / (valores.size - 1)
            val guion = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
            umbralAdvertencia?.let {
                drawLine(Color(0xFFEF8F00), Offset(0f, y(it)), Offset(size.width, y(it)), strokeWidth = 2f, pathEffect = guion)
            }
            umbralCritico?.let {
                drawLine(Color(0xFFC62828), Offset(0f, y(it)), Offset(size.width, y(it)), strokeWidth = 2f, pathEffect = guion)
            }
            val tramos = (valores.size - 1) * progreso.value
            for (i in 0 until valores.size - 1) {
                if (i >= tramos) break
                drawLine(
                    color,
                    Offset(i * paso, y(valores[i])),
                    Offset((i + 1) * paso, y(valores[i + 1])),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
