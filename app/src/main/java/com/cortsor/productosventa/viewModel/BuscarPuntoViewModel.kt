package com.cortsor.productosventa.viewModel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BuscarPuntoViewModel : ViewModel() {
    var ip by mutableStateOf("")
}