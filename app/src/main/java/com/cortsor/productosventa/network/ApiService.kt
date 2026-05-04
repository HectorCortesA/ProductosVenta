package com.cortsor.productosventa.network

import com.cortsor.productosventa.model.CategoryResponse
import com.cortsor.productosventa.model.CreateProductResponse
import com.cortsor.productosventa.model.ProductRequest
import com.cortsor.productosventa.model.ProductResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 1. DEFINICIÓN DE RUTAS (Tu Interfaz)
interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductResponse>

    @POST("products")
    suspend fun createProduct(@Body request: ProductRequest): CreateProductResponse

    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("ping")
    suspend fun ping(): Map<String, Any>
}

// 2. CONFIGURACIÓN DINÁMICA (Tu Cliente)
object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var currentIp: String = "127.0.0.1"

    // Esta es la función que llamas desde tu pantalla "Buscar Punto"
    fun updateIp(ip: String) {
        if (currentIp != ip) {
            currentIp = ip
            retrofit = null // Al ponerlo en null, forzamos que se reconecte a la nueva IP
        }
    }

    // ✅ CORRECCIÓN: Quitamos el import de Firebase que tenías aquí
    val instance: ApiService
        get() {
            if (retrofit == null) {
                val url = "http://$currentIp:3001/"

                retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            // Aquí usamos la interfaz ApiService definida arriba
            return retrofit!!.create(ApiService::class.java)
        }
}