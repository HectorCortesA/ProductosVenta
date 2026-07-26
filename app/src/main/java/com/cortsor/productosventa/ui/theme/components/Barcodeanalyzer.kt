package com.cortsor.productosventa.ui.theme.components

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Analizador de frames de CameraX que usa ML Kit para detectar
 * códigos de barras (EAN, UPC, Code128, Code39, QR) en tiempo real.
 *
 * Cada vez que detecta un valor, lo reporta mediante [onBarcodeDetected].
 * No filtra duplicados aquí: ese trabajo lo hace el ViewModel (debounce),
 * porque este analizador corre en un hilo de fondo y se llama muchas
 * veces por segundo.
 */
class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }
                    ?.rawValue
                    ?.let(onBarcodeDetected)
            }
            .addOnFailureListener {
                // Un frame fallido no es crítico, el siguiente puede funcionar bien.
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}