package com.cortsor.productosventa.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType // ¡Nueva importación necesaria!
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.viewModel.BuscarPuntoViewModel

@Composable
fun BuscarPuntoVentaScreen(
    onNavigateToAddProduct: () -> Unit,
    viewModel: BuscarPuntoViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Manteniendo tu fondo blanco impecable
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 100.dp)
        ) {
            Text(
                text = "Buscar punto de venta",
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 28.dp)
            )
            OutlinedTextField(
                value = viewModel.ip,
                onValueChange = { newValue ->
                    // FILTRO: Solo permite números y puntos
                    val filteredIp = newValue.filter { it.isDigit() || it == '.' }
                    viewModel.ip = filteredIp
                },
                placeholder = { Text("Ingrese IP", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                textStyle = TextStyle(textAlign = TextAlign.Center, color = Color.Black, fontSize = 15.sp),
                singleLine = true,
                shape = RoundedCornerShape(5.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                    focusedBorderColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, // Fuerza a abrir el teclado numérico
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    if (viewModel.ip.isNotBlank()) onNavigateToAddProduct()
                }),
                modifier = Modifier
                    .height(50.dp)
                    .width(153.dp)
            )

            // Espacio entre el input y el botón
            Spacer(modifier = Modifier.height(24.dp))

            // NUEVO BOTÓN "ACCEDER"
            Button(
                onClick = {
                    if (viewModel.ip.isNotBlank()) onNavigateToAddProduct()
                },
                modifier = Modifier
                    .height(45.dp)
                    .width(153.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF)), // Verde de tu app
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Acceder", color = Color.White, fontSize = 15.sp)
            }
        }
    }
}