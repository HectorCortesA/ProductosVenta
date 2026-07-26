package com.cortsor.productosventa.ui.theme.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cortsor.productosventa.viewModel.ScannerProductoViewModel
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val ScannerGreen = Color(0xFF62C3AF) // Usando el verde del tema
private val CaptureButtonFill = Color(0xFF1F1F1F)
private val CaptureButtonStroke = Color.White

/**
 * Modal transparente para escanear el SKU/código de barras.
 */
@Composable
fun ScannerProducto(
    onSkuScanned: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: ScannerProductoViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Resetear el estado del ViewModel al entrar
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    // Manejo de desenfoque del fondo (Similar a PhotoModal)
    val view = LocalView.current
    SideEffect {
        val window = (view.parent as? DialogWindowProvider)?.window
        window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                it.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                it.attributes.blurBehindRadius = 40
            }
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            viewModel.setError("Permiso de cámara denegado.")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraProviderRef?.unbindAll()
            cameraExecutor.shutdown()
        }
    }

    fun onConfirmedSku(sku: String) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.reset() // Resetear antes de cerrar para la próxima vez
        onSkuScanned(sku)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                Text("Escanear Producto", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF5F5F5))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission && viewModel.errorMsg == null) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            startCamera(
                                context = ctx,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                cameraExecutor = cameraExecutor,
                                onImageCaptureReady = { imageCapture = it },
                                onProviderReady = { cameraProviderRef = it },
                                onBarcodeDetected = { value ->
                                    viewModel.onBarcodeDetected(value) { onConfirmedSku(it) }
                                },
                                onError = { message -> viewModel.setError(message) }
                            )
                            previewView
                        }
                    )
                    ScannerOverlay(modifier = Modifier.fillMaxSize(), highlighted = viewModel.detectedCode != null)
                } else if (viewModel.errorMsg != null) {
                    CameraErrorContent(message = viewModel.errorMsg!!, onRetry = {
                        viewModel.setError(null)
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    })
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = viewModel.detectedCode?.let { "Código: $it" } ?: "Enfoca el código de barras",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            CaptureButton(
                enabled = !viewModel.isProcessing,
                onClick = {
                    if (viewModel.isProcessing) return@CaptureButton
                    if (viewModel.detectedCode != null) {
                        viewModel.confirmDetectedNow { onConfirmedSku(it) }
                    } else if (imageCapture != null) {
                        captureAndRunOcr(
                            imageCapture = imageCapture!!,
                            executor = cameraExecutor,
                            scope = scope,
                            onResult = { value ->
                                viewModel.onOcrResult(
                                    value = value,
                                    onConfirmed = { onConfirmedSku(it) },
                                    onNotFound = { viewModel.setError("No se detectó ningún código.") }
                                )
                            }
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("O pulsa para capturar", fontSize = 12.sp, color = Color.LightGray)
        }
    }
}

@Composable
private fun ScannerOverlay(modifier: Modifier = Modifier, highlighted: Boolean) {
    val lineColor = if (highlighted) Color(0xFF2E7D32) else Color.Black
    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val cornerLength = size.width * 0.12f
        val margin = 12.dp.toPx()

        // Esquina superior izquierda
        drawLine(lineColor, Offset(margin, margin), Offset(margin + cornerLength, margin), strokeWidth, StrokeCap.Round)
        drawLine(lineColor, Offset(margin, margin), Offset(margin, margin + cornerLength), strokeWidth, StrokeCap.Round)

        // Esquina superior derecha
        drawLine(lineColor, Offset(size.width - margin, margin), Offset(size.width - margin - cornerLength, margin), strokeWidth, StrokeCap.Round)
        drawLine(lineColor, Offset(size.width - margin, margin), Offset(size.width - margin, margin + cornerLength), strokeWidth, StrokeCap.Round)

        // Esquina inferior izquierda
        drawLine(lineColor, Offset(margin, size.height - margin), Offset(margin + cornerLength, size.height - margin), strokeWidth, StrokeCap.Round)
        drawLine(lineColor, Offset(margin, size.height - margin), Offset(margin, size.height - margin - cornerLength), strokeWidth, StrokeCap.Round)

        // Esquina inferior derecha
        drawLine(lineColor, Offset(size.width - margin, size.height - margin), Offset(size.width - margin - cornerLength, size.height - margin), strokeWidth, StrokeCap.Round)
        drawLine(lineColor, Offset(size.width - margin, size.height - margin), Offset(size.width - margin, size.height - margin - cornerLength), strokeWidth, StrokeCap.Round)
    }
}

@Composable
private fun CaptureButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(width = 125.dp, height = 122.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (enabled) CaptureButtonFill else CaptureButtonFill.copy(alpha = 0.5f))
                    .border(width = 5.dp, color = CaptureButtonStroke, shape = RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun CameraErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color(0xFFD32F2F),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F1F), contentColor = Color.White)
        ) {
            Text("Reintentar", fontSize = 12.sp)
        }
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraExecutor: ExecutorService,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onProviderReady: (ProcessCameraProvider) -> Unit,
    onBarcodeDetected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(onBarcodeDetected))
                }

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis,
                imageCapture
            )

            onProviderReady(cameraProvider)
            onImageCaptureReady(imageCapture)
        } catch (e: Exception) {
            onError("No se pudo iniciar la cámara.")
        }
    }, ContextCompat.getMainExecutor(context))
}

@OptIn(ExperimentalGetImage::class)
private fun captureAndRunOcr(
    imageCapture: ImageCapture,
    executor: ExecutorService,
    scope: CoroutineScope,
    onResult: (String?) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                val mediaImage = image.image
                if (mediaImage == null) {
                    image.close()
                    onResult(null)
                    return
                }
                val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                scope.launch {
                    val digits = SkuTextRecognizer.extractDigitsFromImage(inputImage)
                    image.close()
                    onResult(digits)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("ScannerProducto", "Error al capturar imagen", exception)
                onResult(null)
            }
        }
    )
}