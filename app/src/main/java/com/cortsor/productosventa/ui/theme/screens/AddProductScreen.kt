package com.cortsor.productosventa.ui.theme.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.ui.theme.ProductosVentaTheme
import com.cortsor.productosventa.ui.theme.components.PhotoModal
import com.cortsor.productosventa.viewModel.AddProductViewModel

@Composable
fun AddProductScreen(viewModel: AddProductViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 38.dp, vertical = 73.dp)
    ) {
        Text("Añadir productos", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(bottom = 32.dp))

        // --- IMAGEN ---
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            Box(
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(19.dp)).background(Color(0xFFD9D9D9))
                    .clickable { viewModel.isPhotoModalOpen = true },
                contentAlignment = Alignment.Center
            ) {
                val bitmap = if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap
                if (bitmap != null) {
                    Image(bitmap.asImageBitmap(), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Text("+", fontSize = 24.sp, color = Color.Gray)
                }
            }
        }

        // --- SKU ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(modifier = Modifier.weight(1f)) {
                FormField(label = "SKU (Opcional)", value = viewModel.sku, onValueChange = { viewModel.sku = it })
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { },
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9F9F9)),
                elevation = ButtonDefaults.buttonElevation(2.dp),
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // --- CATEGORÍA ---
        Text("Categoría", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = if (viewModel.isLoadingCategories) "Cargando..." else viewModel.selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(5.dp),
                    trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF9F9F9))
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    viewModel.categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { viewModel.selectedCategory = cat; expanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.isAddingCategory = true },
                modifier = Modifier.size(50.dp).background(Color(0xFF62C3AF), RoundedCornerShape(5.dp))
            ) { Icon(Icons.Default.Add, null, tint = Color.White) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TIPO VENTA ---
        Row(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD9D9D9)).padding(4.dp)) {
            SaleTypeButton("Por pieza", viewModel.saleType == "unit", Modifier.weight(1f)) {
                viewModel.saleType = "unit"
                viewModel.sellUnit = "pz"
            }
            SaleTypeButton("A granel", viewModel.saleType == "bulk", Modifier.weight(1f)) {
                viewModel.saleType = "bulk"
                viewModel.sellUnit = "KG"
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FormField(label = "Nombre *", value = viewModel.name, onValueChange = { viewModel.name = it })
        Spacer(modifier = Modifier.height(24.dp))
        FormField(label = "Descripción", value = viewModel.description, onValueChange = { viewModel.description = it })
        Spacer(modifier = Modifier.height(24.dp))

        // --- PRECIOS Y STOCK MÍNIMO ---
        if (viewModel.saleType == "unit") {
            FormField(label = "Precio", value = viewModel.price, onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Stock (piezas)", value = viewModel.quantity, onValueChange = { viewModel.quantity = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Stock Mínimo", value = viewModel.minQuantity, onValueChange = { viewModel.minQuantity = it })
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    UnitChip(unit, viewModel.sellUnit == unit) { viewModel.sellUnit = unit }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Precio por ${viewModel.sellUnit.lowercase()}", value = viewModel.price, onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Stock (${viewModel.sellUnit})", value = viewModel.quantity, onValueChange = { viewModel.quantity = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(label = "Stock Mínimo (${viewModel.sellUnit})", value = viewModel.minQuantity, onValueChange = { viewModel.minQuantity = it })
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { viewModel.saveProduct(context) },
            modifier = Modifier.align(Alignment.CenterHorizontally).height(45.dp).width(180.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9F9F9)),
            elevation = ButtonDefaults.buttonElevation(2.dp)
        ) {
            Text("Guardar Producto", color = Color.Black)
        }
    }

    if (viewModel.isAddingCategory) {
        AlertDialog(
            onDismissRequest = { viewModel.isAddingCategory = false },
            title = { Text("Nueva Categoría") },
            text = { OutlinedTextField(value = viewModel.newCategoryName, onValueChange = { viewModel.newCategoryName = it }) },
            confirmButton = { TextButton(onClick = { viewModel.addCategory() }) { Text("Agregar") } }
        )
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    ProductosVentaTheme {
        AddProductScreen()
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF9F9F9), unfocusedBorderColor = Color.Black.copy(0.1f))
        )
    }
}

@Composable
fun SaleTypeButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color.White.copy(0.5f) else Color.Transparent).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Text(text, fontSize = 14.sp) }
}

@Composable
fun UnitChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(50.dp, 40.dp).clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF62C3AF) else Color(0xFFF9F9F9))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Text(text, color = if (isSelected) Color.White else Color.Black) }
}