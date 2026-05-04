package com.cortsor.productosventa.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

class AddProductViewModel : ViewModel() {
    var saleType by mutableStateOf("unit")
    var name by mutableStateOf("")
    var price by mutableStateOf("")
    var quantity by mutableStateOf("")
    var minQuantity by mutableStateOf("")
    var sellUnit by mutableStateOf("KG")

    // Estados de la imagen
    var originalBitmap by mutableStateOf<Bitmap?>(null)
    var noBgBitmap by mutableStateOf<Bitmap?>(null)
    var bgMode by mutableStateOf("con") // "con" o "sin" fondo
    var isPhotoModalOpen by mutableStateOf(false)

    // 1. Solo carga la imagen cuando la seleccionas (Se queda "Con fondo")
    fun onImageSelected(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            // IMPORTANTE: Se fuerza el formato ARGB_8888 para que ML Kit no lance error
            val bitmap = BitmapFactory.decodeStream(inputStream)?.copy(Bitmap.Config.ARGB_8888, true)

            if (bitmap != null) {
                originalBitmap = bitmap
                noBgBitmap = null // Resetea la versión sin fondo anterior si sube una foto nueva
                bgMode = "con"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Ejecuta la IA solo cuando haces clic en el botón "Sin fondo"
    fun removeBackground(context: Context) {
        // Si ya había procesado esta imagen antes, no repite el trabajo, solo la muestra
        if (noBgBitmap != null) {
            bgMode = "sin"
            return
        }

        val currentBitmap = originalBitmap
        if (currentBitmap == null) {
            Toast.makeText(context, "Primero selecciona una foto", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            Toast.makeText(context, "Procesando imagen...", Toast.LENGTH_SHORT).show()

            val image = InputImage.fromBitmap(currentBitmap, 0)
            val options = SubjectSegmenterOptions.Builder()
                .enableForegroundBitmap() // Esto extrae el objeto sin fondo
                .build()

            val segmenter = SubjectSegmentation.getClient(options)

            segmenter.process(image)
                .addOnSuccessListener { result ->
                    noBgBitmap = result.foregroundBitmap
                    bgMode = "sin" // Cambia la vista automáticamente al terminar
                    Toast.makeText(context, "Fondo eliminado con éxito", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(context, "Error al procesar el fondo", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error de la IA al eliminar el fondo", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Guardar y resetear
    fun saveProduct(context: Context) {
        if (name.isBlank() || price.isBlank()) {
            Toast.makeText(context, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "¡Producto '$name' guardado exitosamente!", Toast.LENGTH_LONG).show()

        // Limpiar formulario tras guardar
        name = ""
        price = ""
        quantity = ""
        minQuantity = ""
        originalBitmap = null
        noBgBitmap = null
        bgMode = "con"
    }
}