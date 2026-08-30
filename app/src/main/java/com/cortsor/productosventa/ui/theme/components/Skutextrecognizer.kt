package com.cortsor.productosventa.ui.theme.components

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


object SkuTextRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractDigitsFromImage(image: InputImage): String? =
        suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    
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