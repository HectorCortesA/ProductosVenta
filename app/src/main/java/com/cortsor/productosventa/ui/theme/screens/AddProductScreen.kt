package com.cortsor.productosventa.ui.theme.screens

package com.tuempresa.tuapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.ui.components.PhotoModal
import com.cortsor.productosventa.viewModel.AddProductViewModel

@Composable
fun AddProductScreen(viewModel: AddProductViewModel = viewModel()) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 38.dp, vertical = 73.dp)
    ) {
        Text("Añadir productos", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(bottom = 32.dp))

        Text("Imagen", fontSize = 15.sp, color = Color.Black)
        Text("Click para subir imagen", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(Color(0xFFD9D9D9))
                    .clickable { viewModel.isPhotoModalOpen = true }
            ) {
                val currentBitmap = if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap
                if (currentBitmap != null) {
                    Image(
                        bitmap = currentBitmap.asImageBitmap(),
                        contentDescription = "Producto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Agregar foto", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Text("Tipo de venta", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(bottom = 12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(4.dp)
        ) {
            SaleTypeButton("Por pieza", viewModel.saleType == "unit", Modifier.weight(1f)) { viewModel.saleType = "unit" }
            SaleTypeButton("A granel", viewModel.saleType == "bulk", Modifier.weight(1f)) { viewModel.saleType = "bulk" }
        }
        if (viewModel.saleType == "bulk") {
            Text("El precio se calcula por peso/volumen.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        FormField(label = "Nombre *", value = viewModel.name, onValueChange = { viewModel.name = it })
        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.saleType == "unit") {
            FormField(label = "Precio", value = viewModel.price, onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Stock (piezas)", value = viewModel.quantity, onValueChange = { viewModel.quantity = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Mínimo (piezas)", value = viewModel.minQuantity, onValueChange = { viewModel.minQuantity = it })
        } else {
            Text("Unidad de venta", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    val isSelected = viewModel.sellUnit == unit
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(39.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isSelected) Color(0xFF62C3AF).copy(alpha = 0.1f) else Color(0xFFF9F9F9))
                            .border(1.dp, if (isSelected) Color(0xFF62C3AF) else Color.Black.copy(alpha = 0.1f), RoundedCornerShape(5.dp))
                            .clickable { viewModel.sellUnit = unit }
                    ) {
                        Text(unit, color = if (isSelected) Color(0xFF62C3AF) else Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Precio por ${viewModel.sellUnit.lowercase()}", value = viewModel.price, onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Stock (${viewModel.sellUnit.lowercase()})", value = viewModel.quantity, onValueChange = { viewModel.quantity = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Mínimo (${viewModel.sellUnit.lowercase()})", value = viewModel.minQuantity, onValueChange = { viewModel.minQuantity = it })
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { /* Guardar lógica */ },
            modifier = Modifier.align(Alignment.CenterHorizontally).height(45.dp).width(150.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9F9F9)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text("Aguardar Producto", color = Color.Black)
        }
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF9F9F9),
                focusedContainerColor = Color(0xFFF9F9F9),
                unfocusedBorderColor = Color.Black.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun SaleTypeButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.5f) else Color.Transparent)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}