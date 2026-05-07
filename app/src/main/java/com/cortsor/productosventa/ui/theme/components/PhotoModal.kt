package com.cortsor.productosventa.ui.theme.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.cortsor.productosventa.viewModel.AddProductViewModel

@Composable
fun PhotoModal(viewModel: AddProductViewModel, onClose: () -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // 1. Obtener el tipo MIME real (ej: image/jpeg, image/png)
            val mimeType = context.contentResolver.getType(it)

            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            }
            // 2. Pasar bitmap y mimeType al ViewModel
            viewModel.processBackgroundRemoval(bitmap, mimeType)
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // --- INICIO CÓDIGO PARA BLUR Y QUITAR EL FONDO OSCURO GENÉRICO ---
        val view = LocalView.current
        SideEffect {
            val window = (view.parent as? DialogWindowProvider)?.window
            window?.let {
                // Quita la sombra genérica
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                // Aplica el blur al fondo de la app (Solo Android 12+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    it.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    it.attributes.blurBehindRadius = 50 // Intensidad del blur
                }

                it.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
        // --- FIN CÓDIGO PARA BLUR ---

        // Contenedor base que ocupa toda la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Tu Modal real con el color negro semitransparente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.Black.copy(alpha = 0.4f)) // Fondo negro transparente con el blur detrás
                    // Opcional: Agregar un pequeño borde ayuda a definir el cristal
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(38.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(48.dp))
                        Text("Foto del Producto", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(19.dp))
                            .clickable { launcher.launch("image/*") }
                    ) {
                        if (viewModel.isProcessing) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            val currentBitmap = if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap
                            if (currentBitmap != null) {
                                Image(
                                    bitmap = currentBitmap.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize().padding(8.dp)
                                )
                            } else {
                                Text("Click para subir", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .width(270.dp)
                            .height(43.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewModel.bgMode == "con") Color.White.copy(alpha = 0.3f) else Color.Transparent)
                                .clickable { viewModel.bgMode = "con" },
                            contentAlignment = Alignment.Center
                        ) { Text("Original", color = Color.White) }

                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewModel.bgMode == "sin") Color.White.copy(alpha = 0.3f) else Color.Transparent)
                                .clickable {
                                    if (viewModel.noBgBitmap != null) {
                                        viewModel.bgMode = "sin"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Quitar fondo",
                                color = if (viewModel.noBgBitmap != null) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onClose,
                        modifier = Modifier.width(184.dp).height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Aceptar", color = Color.Black, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

