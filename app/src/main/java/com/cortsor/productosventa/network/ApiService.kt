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
object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var currentIp: String = "127.0.0.1"

    fun updateIp(ip: String) {
        if (currentIp != ip) {
            currentIp = ip
            retrofit = null 
        }
    }
    val instance: ApiService
        get() {
            if (retrofit == null) {
                val url = "http://$currentIp:3001/"

                retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(ApiService::class.java)
        }
}