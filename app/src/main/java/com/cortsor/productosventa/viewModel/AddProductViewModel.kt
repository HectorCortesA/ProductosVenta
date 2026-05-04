package com.cortsor.productosventa.viewModel // Todo en minúsculas por convención

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.ui.components.PhotoModal

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
        Text(
            "Añadir productos",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("Imagen", fontSize = 15.sp, color = Color.Black)
        Text(
            "Click para subir imagen",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

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
                // Lógica de selección de imagen corregida
                val currentBitmap =
                    if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap

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

        // --- TIPO DE VENTA ---
        Text(
            "Tipo de venta",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(4.dp)
        ) {
            SaleTypeButton(
                "Por pieza",
                viewModel.saleType == "unit",
                Modifier.weight(1f)
            ) { viewModel.saleType = "unit" }
            SaleTypeButton(
                "A granel",
                viewModel.saleType == "bulk",
                Modifier.weight(1f)
            ) { viewModel.saleType = "bulk" }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FORMULARIO DINÁMICO ---
        FormField(
            label = "Nombre *",
            value = viewModel.name,
            onValueChange = { viewModel.name = it })
        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.saleType == "unit") {
            FormField(
                label = "Precio",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Stock (piezas)",
                value = viewModel.quantity,
                onValueChange = { viewModel.quantity = it })
        } else {
            // Diseño para Granel
            Text("Unidad de venta", fontSize = 15.sp, color = Color.Black)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    UnitChip(unit, viewModel.sellUnit == unit) { viewModel.sellUnit = unit }
                }
            }
            FormField(
                label = "Precio por ${viewModel.sellUnit}",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Lógica de guardado */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF))
        ) {
            Text("Guardar Producto", color = Color.White)
        }
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

@Composable
fun UnitChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp, 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF62C3AF) else Color(0xFFF9F9F9))
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        Text(text, color = if (isSelected) Color.White else Color.Black)
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            label,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SaleTypeButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

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
        Text(
            "Añadir productos",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("Imagen", fontSize = 15.sp, color = Color.Black)
        Text(
            "Click para subir imagen",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

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
                // Lógica de selección de imagen corregida
                val currentBitmap =
                    if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap

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

        // --- TIPO DE VENTA ---
        Text(
            "Tipo de venta",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(4.dp)
        ) {
            SaleTypeButton(
                "Por pieza",
                viewModel.saleType == "unit",
                Modifier.weight(1f)
            ) { viewModel.saleType = "unit" }
            SaleTypeButton(
                "A granel",
                viewModel.saleType == "bulk",
                Modifier.weight(1f)
            ) { viewModel.saleType = "bulk" }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FORMULARIO DINÁMICO ---
        FormField(
            label = "Nombre *",
            value = viewModel.name,
            onValueChange = { viewModel.name = it })
        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.saleType == "unit") {
            FormField(
                label = "Precio",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Stock (piezas)",
                value = viewModel.quantity,
                onValueChange = { viewModel.quantity = it })
        } else {
            // Diseño para Granel
            Text("Unidad de venta", fontSize = 15.sp, color = Color.Black)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    UnitChip(unit, viewModel.sellUnit == unit) { viewModel.sellUnit = unit }
                }
            }
            FormField(
                label = "Precio por ${viewModel.sellUnit}",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Lógica de guardado */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF))
        ) {
            Text("Guardar Producto", color = Color.White)
        }
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

@Composable
fun UnitChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp, 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF62C3AF) else Color(0xFFF9F9F9))
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        Text(text, color = if (isSelected) Color.White else Color.Black)
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            label,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SaleTypeButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

// 1. EL VIEWMODEL (Lógica)
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
}

// 2. LA PANTALLA (Interfaz)
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
        Text(
            "Añadir productos",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("Imagen", fontSize = 15.sp, color = Color.Black)
        Text(
            "Click para subir imagen",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

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
                val currentBitmap =
                    if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap

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

        // --- TIPO DE VENTA ---
        Text(
            "Tipo de venta",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(4.dp)
        ) {
            SaleTypeButton(
                "Por pieza",
                viewModel.saleType == "unit",
                Modifier.weight(1f)
            ) { viewModel.saleType = "unit" }
            SaleTypeButton(
                "A granel",
                viewModel.saleType == "bulk",
                Modifier.weight(1f)
            ) { viewModel.saleType = "bulk" }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FORMULARIO ---
        FormField(
            label = "Nombre *",
            value = viewModel.name,
            onValueChange = { viewModel.name = it })

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.saleType == "unit") {
            FormField(
                label = "Precio",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Stock (piezas)",
                value = viewModel.quantity,
                onValueChange = { viewModel.quantity = it })
        } else {
            Text("Unidad de venta", fontSize = 15.sp, color = Color.Black)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    UnitChip(unit, viewModel.sellUnit == unit) { viewModel.sellUnit = unit }
                }
            }
            FormField(
                label = "Precio por ${viewModel.sellUnit}",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Lógica de guardado */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF))
        ) {
            Text("Guardar Producto", color = Color.White)
        }
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

// 3. COMPONENTES DE APOYO
@Composable
fun UnitChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp, 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF62C3AF) else Color(0xFFF9F9F9))
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        Text(text, color = if (isSelected) Color.White else Color.Black)
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            label,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SaleTypeButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.ui.components.PhotoModal
import com.cortsor.productosventa.viewmodel.AddProductViewModel

class AddProductViewModel : ViewModel() {
    // Estados de la UI
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
}

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
        Text(
            "Añadir productos",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("Imagen", fontSize = 15.sp, color = Color.Black)
        Text(
            "Click para subir imagen",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

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
                // Lógica de selección de imagen corregida
                val currentBitmap =
                    if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap

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

        // --- TIPO DE VENTA ---
        Text(
            "Tipo de venta",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(4.dp)
        ) {
            SaleTypeButton(
                "Por pieza",
                viewModel.saleType == "unit",
                Modifier.weight(1f)
            ) { viewModel.saleType = "unit" }
            SaleTypeButton(
                "A granel",
                viewModel.saleType == "bulk",
                Modifier.weight(1f)
            ) { viewModel.saleType = "bulk" }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FORMULARIO DINÁMICO ---
        FormField(
            label = "Nombre *",
            value = viewModel.name,
            onValueChange = { viewModel.name = it })
        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.saleType == "unit") {
            FormField(
                label = "Precio",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Stock (piezas)",
                value = viewModel.quantity,
                onValueChange = { viewModel.quantity = it })
        } else {
            // Diseño para Granel
            Text("Unidad de venta", fontSize = 15.sp, color = Color.Black)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    UnitChip(unit, viewModel.sellUnit == unit) { viewModel.sellUnit = unit }
                }
            }
            FormField(
                label = "Precio por ${viewModel.sellUnit}",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Lógica de guardado */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF))
        ) {
            Text("Guardar Producto", color = Color.White)
        }
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

@Composable
fun UnitChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp, 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF62C3AF) else Color(0xFFF9F9F9))
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        Text(text, color = if (isSelected) Color.White else Color.Black)
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            label,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SaleTypeButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

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
        Text(
            "Añadir productos",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("Imagen", fontSize = 15.sp, color = Color.Black)
        Text(
            "Click para subir imagen",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

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
                // Lógica de selección de imagen corregida
                val currentBitmap =
                    if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap

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

        // --- TIPO DE VENTA ---
        Text(
            "Tipo de venta",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFD9D9D9))
                .padding(4.dp)
        ) {
            SaleTypeButton(
                "Por pieza",
                viewModel.saleType == "unit",
                Modifier.weight(1f)
            ) { viewModel.saleType = "unit" }
            SaleTypeButton(
                "A granel",
                viewModel.saleType == "bulk",
                Modifier.weight(1f)
            ) { viewModel.saleType = "bulk" }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FORMULARIO DINÁMICO ---
        FormField(
            label = "Nombre *",
            value = viewModel.name,
            onValueChange = { viewModel.name = it })
        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.saleType == "unit") {
            FormField(
                label = "Precio",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Stock (piezas)",
                value = viewModel.quantity,
                onValueChange = { viewModel.quantity = it })
        } else {
            // Diseño para Granel
            Text("Unidad de venta", fontSize = 15.sp, color = Color.Black)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("G", "KG", "ML", "L").forEach { unit ->
                    UnitChip(unit, viewModel.sellUnit == unit) { viewModel.sellUnit = unit }
                }
            }
            FormField(
                label = "Precio por ${viewModel.sellUnit}",
                value = viewModel.price,
                onValueChange = { viewModel.price = it })
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { /* Lógica de guardado */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF62C3AF))
        ) {
            Text("Guardar Producto", color = Color.White)
        }
    }

    if (viewModel.isPhotoModalOpen) {
        PhotoModal(viewModel = viewModel, onClose = { viewModel.isPhotoModalOpen = false })
    }
}

@Composable
fun UnitChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp, 40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF62C3AF) else Color(0xFFF9F9F9))
            .clickable { onClick() }
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        Text(text, color = if (isSelected) Color.White else Color.Black)
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            label,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun SaleTypeButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}