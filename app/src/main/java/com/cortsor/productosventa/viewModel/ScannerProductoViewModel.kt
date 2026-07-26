package com.cortsor.productosventa.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * Estado del modal ScannerProducto.
 *
 * No maneja directamente la cámara (eso vive en la Composable, atado al
 * lifecycle de CameraX); solo guarda qué se ha detectado y en qué modo
 * está la pantalla (cámara real, error, o simulada).
 */
class ScannerProductoViewModel : ViewModel() {

    var errorMsg by mutableStateOf<String?>(null)
        private set

    var detectedCode by mutableStateOf<String?>(null)
        private set

    var isProcessing by mutableStateOf(false)
        private set

    private var lastRawValue: String? = null
    private var consecutiveCount = 0
    private val requiredConsecutiveDetections = 2

    /**
     * Se llama en cada frame donde ML Kit encontró un código de barras.
     */
    fun onBarcodeDetected(value: String, onConfirmed: (String) -> Unit) {
        if (isProcessing) return

        if (value == lastRawValue) {
            consecutiveCount++
        } else {
            lastRawValue = value
            consecutiveCount = 1
        }
        detectedCode = value

        if (consecutiveCount >= requiredConsecutiveDetections) {
            isProcessing = true
            onConfirmed(value)
        }
    }

    /** Confirma manualmente lo último detectado. */
    fun confirmDetectedNow(onConfirmed: (String) -> Unit) {
        val value = detectedCode ?: return
        if (isProcessing) return
        isProcessing = true
        onConfirmed(value)
    }

    /** Resultado del fallback de OCR. */
    fun onOcrResult(value: String?, onConfirmed: (String) -> Unit, onNotFound: () -> Unit) {
        if (value.isNullOrBlank()) {
            onNotFound()
            return
        }
        isProcessing = true
        onConfirmed(value)
    }

    fun setError(message: String?) {
        errorMsg = message
    }

    fun reset() {
        errorMsg = null
        detectedCode = null
        isProcessing = false
        lastRawValue = null
        consecutiveCount = 0
    }
}
