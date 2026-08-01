package com.mj.homelibrary.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.mj.homelibrary.R

private val ScannerBackground = Color(0xFF0C0906)
private val ScannerText = Color(0xFFF4EADA)
private val ScannerAccent = Color(0xFFE4BE7A)
private val ScannerAccentOn = Color(0xFF3E2800)

@Composable
fun BarcodeScannerSheet(
    onBarcode: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    var bulkScan by remember { mutableStateOf(true) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        granted = isGranted
    }

    LaunchedEffect(Unit) {
        if (!granted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = ScannerBackground, contentColor = ScannerText) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 26.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScannerCircleButton(icon = Icons.Outlined.Close, onClick = onDismiss, contentDescription = stringResource(R.string.content_description_close))
                    Text(stringResource(R.string.action_scan_isbn), fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    ScannerCircleButton(icon = Icons.Outlined.FlashlightOn, onClick = {}, contentDescription = null)
                }

                if (granted) {
                    ScannerViewfinder(onBarcode = onBarcode)
                } else {
                    PermissionPanel(onGrant = { permissionLauncher.launch(Manifest.permission.CAMERA) })
                }

                Text(
                    text = stringResource(R.string.scanner_instruction_body),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = ScannerText.copy(alpha = 0.78f),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, ScannerAccent.copy(alpha = 0.30f), RoundedCornerShape(18.dp))
                        .background(ScannerAccent.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Switch(checked = bulkScan, onCheckedChange = { bulkScan = it })
                    Text(stringResource(R.string.bulk_scan), modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.bulk_scan_count, 0), color = ScannerText.copy(alpha = 0.72f), fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScannerAccent, contentColor = ScannerAccentOn),
                ) {
                    Text(stringResource(R.string.review_books, 0))
                }
            }
        }
    }
}

@Composable
private fun ScannerCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.10f), contentColor = ScannerText),
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
private fun PermissionPanel(onGrant: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(Color(0xFF241A12), RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, modifier = Modifier.size(52.dp), tint = ScannerAccent)
        Text(stringResource(R.string.scanner_permission_needed), color = ScannerText)
        Button(onClick = onGrant, colors = ButtonDefaults.buttonColors(containerColor = ScannerAccent, contentColor = ScannerAccentOn)) {
            Text(stringResource(R.string.action_grant_permission))
        }
    }
}

@Composable
private fun ScannerViewfinder(onBarcode: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(
                Brush.linearGradient(listOf(Color(0xFF241A12), Color(0xFF120D09), Color(0xFF1C140E))),
                RoundedCornerShape(24.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        CameraBarcodePreview(onBarcode)
        ScannerReticle(modifier = Modifier.fillMaxSize().padding(26.dp))
    }
}

@Composable
private fun ScannerReticle(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scanner")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2100), repeatMode = RepeatMode.Reverse),
        label = "scannerSweep",
    )
    Canvas(modifier = modifier) {
        drawRoundRect(
            color = ScannerAccent.copy(alpha = 0.35f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
            cornerRadius = CornerRadius(16.dp.toPx()),
        )
        val corner = 34.dp.toPx()
        val stroke = 3.dp.toPx()
        val w = size.width
        val h = size.height
        fun line(start: Offset, end: Offset) = drawLine(ScannerAccent, start, end, strokeWidth = stroke)
        line(Offset.Zero, Offset(corner, 0f)); line(Offset.Zero, Offset(0f, corner))
        line(Offset(w, 0f), Offset(w - corner, 0f)); line(Offset(w, 0f), Offset(w, corner))
        line(Offset(0f, h), Offset(corner, h)); line(Offset(0f, h), Offset(0f, h - corner))
        line(Offset(w, h), Offset(w - corner, h)); line(Offset(w, h), Offset(w, h - corner))
        val y = h * sweep
        drawRect(
            brush = Brush.horizontalGradient(listOf(Color.Transparent, ScannerAccent, Color.Transparent)),
            topLeft = Offset(0f, y),
            size = Size(w, 2.dp.toPx()),
        )
    }
}

@Composable
private fun CameraBarcodePreview(onBarcode: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var handled by remember { mutableStateOf(false) }
    val scanner = remember { BarcodeScanning.getClient() }

    DisposableEffect(Unit) {
        onDispose { scanner.close() }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { viewContext ->
            PreviewView(viewContext).apply {
                alpha = 0.35f
                val providerFuture = ProcessCameraProvider.getInstance(viewContext)
                providerFuture.addListener(
                    {
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(surfaceProvider)
                        }
                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { imageAnalysis ->
                                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                    processBarcodeImage(
                                        imageProxy = imageProxy,
                                        scanner = scanner,
                                        handled = handled,
                                        onHandled = { handled = true },
                                        onBarcode = onBarcode,
                                    )
                                }
                            }

                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analysis,
                        )
                    },
                    ContextCompat.getMainExecutor(viewContext),
                )
            }
        },
    )
}

@OptIn(ExperimentalGetImage::class)
private fun processBarcodeImage(
    imageProxy: ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    handled: Boolean,
    onHandled: () -> Unit,
    onBarcode: (String) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null || handled) {
        imageProxy.close()
        return
    }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val rawValue = barcodes.firstOrNull()?.rawValue?.filter(Char::isDigit)
            if (!rawValue.isNullOrBlank()) {
                onHandled()
                onBarcode(rawValue)
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
