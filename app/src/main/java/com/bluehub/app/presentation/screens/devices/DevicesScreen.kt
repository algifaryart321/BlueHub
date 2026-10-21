package com.bluehub.app.presentation.screens.devices

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluehub.app.core.service.BluetoothForegroundService
import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.domain.model.DeviceCategory

private val FigmaBgBlack = Color(0xFF0A0A0A)
private val FigmaCardDark = Color(0xFF1C1C1C)
private val FigmaGridCardDark = Color(0xFF222222)
private val FigmaBlue = Color(0xFF2575FC)
private val FigmaConnectedDark = Color(0xFF111111)

/**
 * 1. HOME SCREEN (Figma Image 1 - Leftmost screen)
 * Displays "BlueHub", "Perangkat Yang Pernah Terhubung", and "Tambah bluetooth" button at bottom.
 */
@Composable
fun HomeScreen(
    viewModel: DevicesViewModel,
    onAddBluetoothClick: () -> Unit,
    onOpenSmartHomeRemote: (BluetoothDeviceDomain) -> Unit
) {
    val savedDevices by viewModel.savedDevices.collectAsState()
    val context = LocalContext.current
    var deviceToDelete by remember { mutableStateOf<BluetoothDeviceDomain?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants[Manifest.permission.BLUETOOTH_CONNECT] == true) {
            BluetoothForegroundService.startService(context)
        }
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    LaunchedEffect(Unit) {
        viewModel.statusMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Long-press Delete Confirmation Dialog
    deviceToDelete?.let { targetDevice ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            containerColor = FigmaGridCardDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = {
                Text(
                    text = "Hapus Perangkat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda ingin menghapus \"${targetDevice.name ?: "Perangkat"}\" dari Perangkat Yang Pernah Terhubung?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavedDevice(targetDevice)
                        deviceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hapus Perangkat", fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deviceToDelete = null }
                ) {
                    Text("Batal", color = Color.LightGray, fontSize = 13.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBgBlack)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BlueHub",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Perangkat Yang Pernah Terhubung",
            color = Color.LightGray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (savedDevices.isEmpty()) {
            // Show default category preview cards if no device has been connected yet
            val defaultCategories = listOf(
                DeviceCategory.SMARTHOME,
                DeviceCategory.KAMERA,
                DeviceCategory.PERIFERAL,
                DeviceCategory.IOT,
                DeviceCategory.PRINTER
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(defaultCategories) { cat ->
                    SavedDeviceCard(
                        name = "Belum ada ${cat.displayTitle} terhubung",
                        category = cat,
                        isConnected = false,
                        onClick = onAddBluetoothClick,
                        onLongClick = {}
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedDevices, key = { it.address }) { dev ->
                    SavedDeviceCard(
                        name = dev.name ?: "Nama Perangkat",
                        category = dev.category,
                        isConnected = dev.isConnected,
                        onClick = {
                            if (dev.category == DeviceCategory.SMARTHOME) {
                                viewModel.connectDevice(dev) {
                                    onOpenSmartHomeRemote(dev)
                                }
                            } else {
                                viewModel.connectDevice(dev)
                            }
                        },
                        onLongClick = {
                            deviceToDelete = dev
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Blue Pill Button: "Tambah bluetooth"
        Button(
            onClick = onAddBluetoothClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FigmaBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Tambah bluetooth",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SavedDeviceCard(
    name: String,
    category: DeviceCategory,
    isConnected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(FigmaCardDark)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            CategoryBadgeIcon(category = category)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isConnected) {
                    Text(
                        text = if (category == DeviceCategory.SMARTHOME) "Terhubung • Ketuk untuk buka remot" else "Terhubung",
                        color = FigmaBlue,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * 2. CATEGORY SELECTION SCREEN (Figma Image 1 - 2nd screen)
 * 2-column grid of categories: smarthome, kamera, periferal, perngkat Iot, printer
 */
@Composable
fun CategorySelectionScreen(
    onBack: () -> Unit,
    onCategorySelected: (DeviceCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBgBlack)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Top Bar: Back Arrow + Centered "BlueHub"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "BlueHub",
                color = Color.White,
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Row 1: smarthome | kamera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryGridBox(
                category = DeviceCategory.SMARTHOME,
                modifier = Modifier.weight(1f),
                onClick = { onCategorySelected(DeviceCategory.SMARTHOME) }
            )
            CategoryGridBox(
                category = DeviceCategory.KAMERA,
                modifier = Modifier.weight(1f),
                onClick = { onCategorySelected(DeviceCategory.KAMERA) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 2: periferal | perngkat Iot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryGridBox(
                category = DeviceCategory.PERIFERAL,
                modifier = Modifier.weight(1f),
                onClick = { onCategorySelected(DeviceCategory.PERIFERAL) }
            )
            CategoryGridBox(
                category = DeviceCategory.IOT,
                modifier = Modifier.weight(1f),
                onClick = { onCategorySelected(DeviceCategory.IOT) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row 3: printer | empty space
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryGridBox(
                category = DeviceCategory.PRINTER,
                modifier = Modifier.weight(1f),
                onClick = { onCategorySelected(DeviceCategory.PRINTER) }
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CategoryGridBox(
    category: DeviceCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(125.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(FigmaGridCardDark)
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getFigmaCategoryIcon(category),
                contentDescription = category.cardLabel,
                tint = FigmaBlue,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = category.cardLabel,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

/**
 * 3. CATEGORY DEVICE SCANNER SCREEN (Figma Image 1 - 3rd screen & Figma Image 2)
 * Lists available Bluetooth devices for the chosen category.
 * - If SMARTHOME: clicking "Hubungkan" or "Terhubung" opens the Smart Home Remote UI!
 * - If other category: clicking "Hubungkan" connects directly and changes button to "Terhubung"!
 */
@Composable
fun CategoryScannerScreen(
    category: DeviceCategory,
    viewModel: DevicesViewModel,
    onBack: () -> Unit,
    onOpenSmartHomeRemote: (BluetoothDeviceDomain) -> Unit
) {
    val devices by viewModel.filteredDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val hideUnnamed by viewModel.hideUnnamed.collectAsState()
    val connectingAddress by viewModel.connectingAddress.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(category) {
        viewModel.selectCategory(category)
        viewModel.startScan()
    }

    LaunchedEffect(Unit) {
        viewModel.statusMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBgBlack)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Top Bar: Back Arrow + Centered "BlueHub" + Scan button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.stopScan()
                    onBack()
                },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "BlueHub",
                color = Color.White,
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = { viewModel.toggleScan() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = FigmaBlue,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle + Filter Unnamed Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.displayTitle,
                color = Color.White,
                fontSize = 15.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hanya bernama",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = hideUnnamed,
                    onCheckedChange = { viewModel.toggleHideUnnamed() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = FigmaBlue,
                        checkedTrackColor = FigmaBlue.copy(alpha = 0.4f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (devices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isScanning) {
                        "Memindai perangkat ${category.displayTitle} terdekat..."
                    } else {
                        "Tidak ada perangkat ${category.displayTitle} di sekitar Anda"
                    },
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(devices, key = { it.address }) { dev ->
                    FigmaDeviceItemCard(
                        device = dev,
                        categoryIcon = category,
                        isConnecting = connectingAddress == dev.address,
                        onActionClick = {
                            if (category == DeviceCategory.SMARTHOME) {
                                // Smart Home: Connect AND open the Smart Home Remote UI!
                                viewModel.connectDevice(dev, categoryOverride = DeviceCategory.SMARTHOME) {
                                    onOpenSmartHomeRemote(dev.copy(category = DeviceCategory.SMARTHOME))
                                }
                            } else {
                                // Other categories: Toggle connect / directly become "Terhubung"
                                if (dev.isConnected) {
                                    viewModel.disconnect(dev.address)
                                } else {
                                    viewModel.connectDevice(dev, categoryOverride = category)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FigmaDeviceItemCard(
    device: BluetoothDeviceDomain,
    categoryIcon: DeviceCategory,
    isConnecting: Boolean,
    onActionClick: () -> Unit
) {
    val displayName = device.name?.trim()?.takeIf { it.isNotEmpty() }
        ?: "Nama Perangkat"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(FigmaCardDark)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            CategoryBadgeIcon(category = categoryIcon)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = displayName,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Action Button: Blue "Hubungkan" or Dark "Terhubung" matching Figma
        Box(
            modifier = Modifier
                .width(116.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (device.isConnected) FigmaConnectedDark else FigmaBlue)
                .clickable(enabled = !isConnecting) { onActionClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (device.isConnected) "Terhubung" else "Hubungkan",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CategoryBadgeIcon(category: DeviceCategory) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = getFigmaCategoryIcon(category),
            contentDescription = category.displayTitle,
            tint = FigmaBlue,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun getFigmaCategoryIcon(category: DeviceCategory): ImageVector {
    return when (category) {
        DeviceCategory.SMARTHOME -> Icons.Default.Home
        DeviceCategory.KAMERA -> Icons.Default.CameraAlt
        DeviceCategory.PERIFERAL -> Icons.Default.Bluetooth
        DeviceCategory.IOT -> Icons.Default.Hub
        DeviceCategory.PRINTER -> Icons.Default.Print
        DeviceCategory.UNKNOWN -> Icons.Default.Bluetooth
    }
}
