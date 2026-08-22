package com.mj.homelibrary.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mj.homelibrary.R
import com.mj.homelibrary.data.validIsbnOrNull
import com.mj.homelibrary.ui.theme.ExpressiveMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun BarcodeScannerSheet(
    onBarcode: (String) -> Unit,
    onDismiss: () -> Unit,
    onBulkScanned: (List<String>) -> Unit = { it.firstOrNull()?.let(onBarcode) },
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    var bulkScan by remember { mutableStateOf(false) }
    var torchOn by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var showManualIsbnDialog by remember { mutableStateOf(false) }
    var scanSuccessTrigger by remember { mutableStateOf(false) }
    val scannedQueue = remember { mutableStateListOf<String>() }
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
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScannerCircleButton(
                        icon = Icons.Outlined.Close,
                        onClick = onDismiss,
                        contentDescription = stringResource(R.string.content_description_close),
                    )
                    Text(
                        stringResource(R.string.action_scan_isbn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ScannerCircleButton(
                            icon = Icons.Outlined.Keyboard,
                            onClick = { showManualIsbnDialog = true },
                            contentDescription = stringResource(R.string.scanner_enter_isbn_manual),
                        )
                        ScannerCircleButton(
                            icon = Icons.Outlined.FlashlightOn,
                            onClick = { torchOn = !torchOn },
                            contentDescription = stringResource(R.string.action_toggle_flashlight),
                            active = torchOn,
                        )
                    }
                }

                if (granted) {
                    ScannerViewfinder(
                        torchOn = torchOn,
                        zoomRatio = zoomRatio,
                        onZoomChange = { zoomRatio = it },
                        scanSuccess = scanSuccessTrigger,
                        onBarcode = { isbn ->
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            scanSuccessTrigger = true
                            if (bulkScan) {
                                if (isbn !in scannedQueue) scannedQueue.add(0, isbn)
                            } else {
                                onBarcode(isbn)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_hero))),
                    )
                } else {
                    PermissionPanel(
                        onGrant = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                }

                LaunchedEffect(scanSuccessTrigger) {
                    if (scanSuccessTrigger) {
                        delay(600)
                        scanSuccessTrigger = false
                    }
                }

                if (bulkScan && scannedQueue.isNotEmpty()) {
                    ScannedTray(
                        items = scannedQueue,
                        onRemove = { scannedQueue.remove(it) },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_lg)))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(dimensionResource(R.dimen.corner_lg)),
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Switch(checked = bulkScan, onCheckedChange = { bulkScan = it })
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.bulk_scan),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            stringResource(R.string.scanner_instruction_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (bulkScan) {
                        Text(
                            stringResource(R.string.bulk_scan_count, scannedQueue.size),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Button(
                    onClick = {
                        if (bulkScan && scannedQueue.isNotEmpty()) {
                            onBulkScanned(scannedQueue.toList())
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
                ) {
                    Text(
                        if (bulkScan && scannedQueue.isNotEmpty()) {
                            stringResource(R.string.review_books, scannedQueue.size)
                        } else {
                            stringResource(R.string.action_cancel)
                        },
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

    if (showManualIsbnDialog) {
        ManualIsbnDialog(
            onDismiss = { showManualIsbnDialog = false },
            onSubmit = { validIsbn ->
                showManualIsbnDialog = false
                if (bulkScan) {
                    if (validIsbn !in scannedQueue) scannedQueue.add(0, validIsbn)
                } else {
                    onBarcode(validIsbn)
                }
            },
        )
    }
}

@Composable
private fun ScannedTray(
    items: List<String>,
    onRemove: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_md)))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.scanner_scanned_tray_title, items.size),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { isbn ->
                FilterChip(
                    selected = true,
                    onClick = { onRemove(isbn) },
                    label = {
                        Text(
                            isbn,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.scanner_remove_scanned, isbn),
                            modifier = Modifier.size(14.dp),
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_control)),
                )
            }
        }
    }
}

@Composable
private fun ManualIsbnDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val validIsbn = remember(text) { text.validIsbnOrNull() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.scanner_enter_isbn_manual), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    stringResource(R.string.scanner_type_isbn_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { char -> char.isDigit() || char.uppercaseChar() == 'X' || char == '-' } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.field_isbn)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            validIsbn?.let(onSubmit)
                        },
                    ),
                    trailingIcon = {
                        if (validIsbn != null) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.isbn_valid_badge),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { validIsbn?.let(onSubmit) },
                enabled = validIsbn != null,
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun ScannerCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    active: Boolean = false,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
private fun PermissionPanel(
    onGrant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(dimensionResource(R.dimen.corner_hero)))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Outlined.QrCodeScanner,
            contentDescription = null,
            modifier = Modifier.size(52.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.scanner_permission_needed),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onGrant) {
            Text(stringResource(R.string.action_grant_permission))
        }
    }
}

@Composable
private fun ScannerViewfinder(
    torchOn: Boolean,
    zoomRatio: Float,
    onZoomChange: (Float) -> Unit,
    scanSuccess: Boolean,
    onBarcode: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tapOffset by remember { mutableStateOf<Offset?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        CameraBarcodePreview(
            torchOn = torchOn,
            zoomRatio = zoomRatio,
            onBarcode = onBarcode,
            onTapFocus = { offset ->
                tapOffset = offset
                coroutineScope.launch {
                    delay(1000)
                    if (tapOffset == offset) tapOffset = null
                }
            },
            onZoomChange = onZoomChange,
        )

        ScannerReticle(
            success = scanSuccess,
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
        )

        tapOffset?.let { offset ->
            FocusRing(
                offset = offset,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                ZoomButton(label = "1x", active = zoomRatio < 1.5f, onClick = { onZoomChange(1.0f) })
                ZoomButton(label = "2x", active = zoomRatio >= 1.5f, onClick = { onZoomChange(2.0f) })
            }
        }
    }
}

@Composable
private fun ZoomButton(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun FocusRing(offset: Offset, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(1.4f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(offset) {
        scale.snapTo(1.4f)
        alpha.snapTo(1f)
        launch { scale.animateTo(1.0f, tween(300, easing = ExpressiveMotion.Emphasized)) }
        launch {
            delay(600)
            alpha.animateTo(0f, tween(400))
        }
    }

    Canvas(modifier = modifier) {
        val radius = 32.dp.toPx() * scale.value
        drawCircle(
            color = Color.White.copy(alpha = alpha.value * 0.9f),
            radius = radius,
            center = offset,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
        )
    }
}

@Composable
private fun ScannerReticle(success: Boolean, modifier: Modifier = Modifier) {
    val accent = if (success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition(label = "scanner")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = ExpressiveMotion.Emphasized),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scannerSweep",
    )
    Canvas(modifier = modifier) {
        drawRoundRect(
            color = accent.copy(alpha = if (success) 0.8f else 0.35f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = if (success) 3.5.dp.toPx() else 2.dp.toPx()),
            cornerRadius = CornerRadius(16.dp.toPx()),
        )
        val corner = 34.dp.toPx()
        val stroke = if (success) 4.5.dp.toPx() else 3.5.dp.toPx()
        val w = size.width
        val h = size.height
        fun line(start: Offset, end: Offset) = drawLine(accent, start, end, strokeWidth = stroke)
        line(Offset.Zero, Offset(corner, 0f)); line(Offset.Zero, Offset(0f, corner))
        line(Offset(w, 0f), Offset(w - corner, 0f)); line(Offset(w, 0f), Offset(w, corner))
        line(Offset(0f, h), Offset(corner, h)); line(Offset(0f, h), Offset(0f, h - corner))
        line(Offset(w, h), Offset(w - corner, h)); line(Offset(w, h), Offset(w, h - corner))
        val y = h * sweep
        drawRect(
            brush = Brush.horizontalGradient(listOf(Color.Transparent, accent, Color.Transparent)),
            topLeft = Offset(0f, y),
            size = Size(w, 2.dp.toPx()),
        )
    }
}

private const val RescanCooldownMillis = 1200L

private class ScanGate {
    private var lastCode: String? = null
    private var lastHandledAt: Long = 0L

    @Synchronized
    fun canHandle(code: String): Boolean {
        val now = System.currentTimeMillis()
        val allowed = code != lastCode || now - lastHandledAt > RescanCooldownMillis
        if (allowed) {
            lastCode = code
            lastHandledAt = now
        }
        return allowed
    }
}

private class CameraBarcodeResources {
    var provider: ProcessCameraProvider? = null
    var preview: Preview? = null
    var analysis: ImageAnalysis? = null

    fun release() {
        analysis?.clearAnalyzer()
        val useCases = listOfNotNull(preview, analysis).toTypedArray()
        if (useCases.isNotEmpty()) provider?.unbind(*useCases)
        provider = null
        preview = null
        analysis = null
    }
}

@Composable
private fun CameraBarcodePreview(
    torchOn: Boolean,
    zoomRatio: Float,
    onBarcode: (String) -> Unit,
    onTapFocus: (Offset) -> Unit,
    onZoomChange: (Float) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var camera by remember { mutableStateOf<Camera?>(null) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    val currentOnBarcode by rememberUpdatedState(onBarcode)
    val scanGate = remember { ScanGate() }
    val resources = remember { CameraBarcodeResources() }
    val disposed = remember { AtomicBoolean(false) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_QR_CODE,
            )
            .build()
        BarcodeScanning.getClient(options)
    }

    DisposableEffect(Unit) {
        onDispose {
            disposed.set(true)
            resources.release()
            scanner.close()
            analysisExecutor.shutdown()
        }
    }

    LaunchedEffect(torchOn, camera) {
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            camera?.cameraControl?.enableTorch(torchOn)
        }
    }

    LaunchedEffect(zoomRatio, camera) {
        camera?.cameraControl?.setZoomRatio(zoomRatio.coerceIn(1.0f, 5.0f))
    }

    val transformState = rememberTransformableState { zoomChange, _, _ ->
        val newZoom = (zoomRatio * zoomChange).coerceIn(1.0f, 5.0f)
        onZoomChange(newZoom)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = transformState)
            .pointerInput(previewViewRef, camera) {
                detectTapGestures { offset ->
                    onTapFocus(offset)
                    val previewView = previewViewRef ?: return@detectTapGestures
                    val pointFactory = previewView.meteringPointFactory
                    val point = pointFactory.createPoint(offset.x, offset.y)
                    val action = FocusMeteringAction.Builder(point).build()
                    camera?.cameraControl?.startFocusAndMetering(action)
                }
            },
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                PreviewView(viewContext).apply {
                    alpha = 1f
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewViewRef = this
                    val providerFuture = ProcessCameraProvider.getInstance(viewContext)
                    providerFuture.addListener(
                        {
                            val provider = providerFuture.get()
                            if (disposed.get()) return@addListener
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(surfaceProvider)
                            }
                            val analysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { imageAnalysis ->
                                    imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                        processBarcodeImage(
                                            imageProxy = imageProxy,
                                            scanner = scanner,
                                            canHandle = scanGate::canHandle,
                                            onBarcode = currentOnBarcode,
                                        )
                                    }
                                }

                            provider.unbindAll()
                            resources.provider = provider
                            resources.preview = preview
                            resources.analysis = analysis
                            camera = provider.bindToLifecycle(
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
}

@OptIn(ExperimentalGetImage::class)
private fun processBarcodeImage(
    imageProxy: ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    canHandle: (String) -> Boolean,
    onBarcode: (String) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val rawValue = barcodes.firstNotNullOfOrNull { it.rawValue?.validIsbnOrNull() }
            if (rawValue != null && canHandle(rawValue)) {
                onBarcode(rawValue)
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
