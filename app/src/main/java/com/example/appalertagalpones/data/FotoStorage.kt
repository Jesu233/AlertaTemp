package com.example.appalertagalpones.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class FotoStorage(private val context: Context) {

    private fun nuevoArchivo(): File {
        val dir = File(context.filesDir, "evidencias").apply { mkdirs() }
        return File(dir, "evidencia_${System.currentTimeMillis()}.jpg")
    }

    fun guardarBitmap(bitmap: Bitmap): String {
        val archivo = nuevoArchivo()
        FileOutputStream(archivo).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        return archivo.absolutePath
    }

    fun guardarDesdeUri(uri: Uri): String? {
        val archivo = nuevoArchivo()
        return try {
            context.contentResolver.openInputStream(uri)?.use { entrada ->
                FileOutputStream(archivo).use { salida -> entrada.copyTo(salida) }
            } ?: return null
            archivo.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}