package com.example.alertagalpones.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.alertagalpones.MainActivity

/** Recurso nativo: notificaciones locales de alerta crítica (pregunta 2 y 4). */
class Notificador(private val context: Context) {
    private val canalId = "alertas_criticas"

    init {
        val canal = NotificationChannel(canalId, "Alertas críticas", NotificationManager.IMPORTANCE_HIGH)
            .apply { description = "Eventos críticos de temperatura y humedad en galpones" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")
    fun alertaCritica(id: Int, titulo: String, texto: String) {
        val permitido = Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!permitido) return

        val abrir = PendingIntent.getActivity(
            context, id, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificacion = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerta crítica: $titulo")
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(abrir)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, notificacion)
    }
}
