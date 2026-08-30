package com.cortsor.productosventa.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.network.RetrofitClient
import com.cortsor.productosventa.viewModel.BuscarPuntoViewModel
import kotlinx.coroutines.launch

@Composable
fun BuscarPuntoVentaScreen(
    onNavigateToAddProduct: () -> Unit,
    viewModel: BuscarPuntoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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
                    
                    val filteredIp = newValue.filter { it.isDigit() || it == '.' }
                    viewModel.ip = filteredIp
                },
                placeholder = {
                    Text(
                        "Ingrese IP",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 15.sp
                ),
                singleLine = true,
                shape = RoundedCornerShape(5.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF9F9F9),
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                    focusedBorderColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = {
                    if (viewModel.ip.isNotBlank()) {
                        conectarServidor(viewModel.ip, scope, context, onNavigateToAddProduct)
                    }
                }),
                modifier = Modifier
                    .height(50.dp)
                    .width(153.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viewModel.ip.isNotBlank()) {
                        conectarServidor(viewModel.ip, scope, context, onNavigateToAddProduct)
                    } else {
                        Toast.makeText(context, "Por favor ingrese una IP", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .height(45.dp)
                    .width(153.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF)),
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Acceder", color = Color.White, fontSize = 15.sp)
            }
        }
    }
}


private fun conectarServidor(
    ip: String,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    onSuccess: () -> Unit
) {
    
    RetrofitClient.updateIp(ip)

    
    scope.launch {
        try {
            
            RetrofitClient.instance.ping()

            
            onSuccess()
        } catch (e: Exception) {
            
            Toast.makeText(
                context,
                "No se pudo conectar al servidor en $ip:3001",
                Toast.LENGTH_LONG
            ).show()

            
            
        }
    }
}