package com.cortsor.productosventa.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cortsor.productosventa.model.*
import com.cortsor.productosventa.network.RetrofitClient
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AddProductViewModel : ViewModel() {
    // Estados del formulario
    var sku by mutableStateOf("")
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    var quantity by mutableStateOf("")
    var minQuantity by mutableStateOf("")
    var saleType by mutableStateOf("unit")
    var sellUnit by mutableStateOf("pz")

    // Gestión de Categorías
    var categories = mutableStateListOf<String>()
    var selectedCategory by mutableStateOf("General")
    var isAddingCategory by mutableStateOf(false)
    var newCategoryName by mutableStateOf("")
    var isLoadingCategories by mutableStateOf(false)

    // Imagen
    var originalBitmap by mutableStateOf<Bitmap?>(null)
    var noBgBitmap by mutableStateOf<Bitmap?>(null)
    var imageMime by mutableStateOf<String?>(null)
    var bgMode by mutableStateOf("con")
    var isPhotoModalOpen by mutableStateOf(false)
    var isProcessing by mutableStateOf(false)

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            isLoadingCategories = true
            try {
                // 1. Obtenemos la lista de objetos del servidor
                val response: List<CategoryResponse> = RetrofitClient.instance.getCategories()

                categories.clear()

                // 2. Extraemos el NOMBRE (it.category)
                val names: List<String> = response.map { it.category }

                if (names.isNotEmpty()) {
                    categories.addAll(names)
                    // Seleccionar la primera por defecto si no hay una válida seleccionada
                    if (selectedCategory == "General" || !names.contains(selectedCategory)) {
                        selectedCategory = names[0]
                    }
                } else {
                    if (!categories.contains("General")) categories.add("General")
                    selectedCategory = "General"
                }
            } catch (e: Exception) {
                if (categories.isEmpty()) {
                    categories.add("General")
                    selectedCategory = "General"
                }
                e.printStackTrace()
            } finally {
                isLoadingCategories = false
            }
        }
    }

    fun addCategory() {
        val trimmed = newCategoryName.trim()
        if (trimmed.isNotEmpty() && !categories.contains(trimmed)) {
            categories.add(trimmed)
            selectedCategory = trimmed
        }
        newCategoryName = ""
        isAddingCategory = false
    }

    fun processBackgroundRemoval(bitmap: Bitmap, mimeType: String?) {
        isProcessing = true
        originalBitmap = bitmap
        noBgBitmap = null
        bgMode = "con"
        imageMime = mimeType

        val image = InputImage.fromBitmap(bitmap, 0)
        val options = SubjectSegmenterOptions.Builder()
            .enableForegroundBitmap()
            .build()
        val segmenter = SubjectSegmentation.getClient(options)
        segmenter.process(image)
            .addOnSuccessListener { result ->
                noBgBitmap = result.foregroundBitmap
                isProcessing = false
                segmenter.close()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                isProcessing = false
                segmenter.close()
            }
    }

    private fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val outputStream = ByteArrayOutputStream()
        val format = if (bgMode == "sin") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        bitmap.compress(format, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun saveProduct(context: Context) {
        if (name.isBlank() || price.isBlank()) {
            Toast.makeText(context, "Nombre y Precio son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            try {
                val bitmapToSend = if (bgMode == "sin") noBgBitmap else originalBitmap
                val minVal = minQuantity.toDoubleOrNull() ?: 0.0
                val finalMime = if (bgMode == "sin") "image/png" else imageMime

                val request = ProductRequest(
                    sku = sku.ifBlank { null },
                    name = name,
                    category = selectedCategory,
                    description = description.ifBlank { null },
                    stock_type = saleType,
                    stock_quantity = if (saleType == "unit") quantity.toDoubleOrNull() ?: 0.0 else 0.0,
                    stock_grams = if (saleType == "bulk") (quantity.toDoubleOrNull() ?: 0.0) * 1000 else 0.0,
                    min_stock_quantity = if (saleType == "unit") minVal else 0.0,
                    min_stock_grams = if (saleType == "bulk") minVal * 1000 else 0.0,
                    price = price.toDoubleOrNull() ?: 0.0,
                    price_per_gram = if (saleType == "bulk") (price.toDoubleOrNull() ?: 0.0) / 1000 else 0.0,
                    display_unit = sellUnit,
                    imageBase64 = bitmapToBase64(bitmapToSend),
                    image_mime = finalMime
                )
                RetrofitClient.instance.createProduct(request)
                Toast.makeText(context, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                clearForm()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearForm() {
        name = ""; price = ""; quantity = ""; minQuantity = ""; sku = ""; description = ""
        originalBitmap = null; noBgBitmap = null; imageMime = null; bgMode = "con"
    }
}