package com.cortsor.productosventa.model

// Lo que envías al servidor
data class ProductRequest(
    val sku: String? = null,
    val name: String,
    val category: String? = null,
    val description: String? = null,
    val stock_type: String = "unit",
    val stock_quantity: Double = 0.0,
    val stock_grams: Double = 0.0,
    val min_stock_quantity: Double = 0.0,
    val min_stock_grams: Double = 0.0,
    val price: Double = 0.0,
    val price_per_gram: Double = 0.0,
    val display_unit: String = "pz",
    val imageBase64: String? = null,
    val image_mime: String? = null // NUEVO: Tipo de imagen (ej: "image/jpeg")
)

// Lo que recibes del servidor
data class ProductResponse(
    val id: Int,
    val sku: String?,
    val name: String,
    val category: String?,
    val price_final: Double,
    val stock: Double,
    val min_stock_quantity: Double?,
    val min_stock_grams: Double?,
    val display_unit: String,
    val imageBase64: String?,
    val image_mime: String? // NUEVO
)
data class CategoryResponse(
    val category: String
)

data class CreateProductResponse(val id: Int)