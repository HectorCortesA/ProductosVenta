package com.cortsor.productosventa.ui.theme.components


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.cortsor.productosventa.viewModel.AddProductViewModel

@Composable
fun PhotoModal(viewModel: AddProductViewModel, onClose: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.processBackgroundRemoval(context, uri)
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
                    .padding(38.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Producto foto", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(19.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(19.dp))
                        .clickable { launcher.launch("image/*") }
                ) {
                    val currentBitmap = if (viewModel.bgMode == "sin") viewModel.noBgBitmap else viewModel.originalBitmap

                    if (currentBitmap != null) {
                        Image(
                            bitmap = currentBitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Tap to add", color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .width(270.dp)
                        .height(43.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF848484))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewModel.bgMode == "con") Color(0xFFD9D9D9) else Color.Transparent)
                            .clickable { viewModel.bgMode = "con" },
                        contentAlignment = Alignment.Center
                    ) { Text("Con fondo", color = if (viewModel.bgMode == "con") Color.Black else Color.White) }

                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewModel.bgMode == "sin") Color(0xFFD9D9D9) else Color.Transparent)
                            .clickable { viewModel.bgMode = "sin" },
                        contentAlignment = Alignment.Center
                    ) { Text("Sin fondo", color = if (viewModel.bgMode == "sin") Color.Black else Color.White) }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onClose,
                    modifier = Modifier.width(184.dp).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Guardar", color = Color.Black, fontSize = 20.sp)
                }
            }
        }
    }
}