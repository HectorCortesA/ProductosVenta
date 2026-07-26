package com.cortsor.productosventa.ui.theme.components

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Fallback para cuando la cámara no logra leer un código de barras: corre
 * reconocimiento de texto (OCR) sobre la foto capturada y extrae la secuencia
 * numérica más larga, asumiendo que corresponde al SKU impreso junto al
 * código de barras (esto es lo que cubre el requisito de "detectar números").
 */
object SkuTextRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractDigitsFromImage(image: InputImage): String? =
        suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    // Buscamos cualquier secuencia numérica de 4 o más dígitos
                    val candidate = Regex("\\d{4,}")
                        .findAll(result.text)
                        .map { it.value }
                        .maxByOrNull { it.length }
                    continuation.resume(candidate)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
}