package com.bluehub.app

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bluehub.app.core.hid.GamepadInputManager
import com.bluehub.app.domain.model.DeviceCategory
import com.bluehub.app.domain.model.SmartHomeRemoteType
import com.bluehub.app.presentation.screens.devices.CategoryScannerScreen
import com.bluehub.app.presentation.screens.devices.CategorySelectionScreen
import com.bluehub.app.presentation.screens.devices.DevicesViewModel
import com.bluehub.app.presentation.screens.devices.HomeScreen
import com.bluehub.app.presentation.screens.remote.SmartHomeRemoteScreen
import com.bluehub.app.presentation.theme.BlueHubTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var gamepadInputManager: GamepadInputManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BlueHubTheme {
                val navController = rememberNavController()
                val viewModel: DevicesViewModel = hiltViewModel()
                var showSplash by rememberSaveable { mutableStateOf(true) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Scaffold(
                        containerColor = Color(0xFF0A0A0A)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0A0A0A))
                                .padding(innerPadding)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "home"
                            ) {
                                // 1. Home Screen ("Perangkat Yang Pernah Terhubung" + "Tambah bluetooth")
                                composable("home") {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onAddBluetoothClick = {
                                            navController.navigate("categories")
                                        },
                                        onOpenSmartHomeRemote = { device ->
                                            val encodedName = Uri.encode(device.name ?: "Smart Home")
                                            navController.navigate("remote/$encodedName")
                                        }
                                    )
                                }

                                // 2. Category Selection Screen (2-column grid matching Figma)
                                composable("categories") {
                                    CategorySelectionScreen(
                                        onBack = { navController.popBackStack() },
                                        onCategorySelected = { category ->
                                            navController.navigate("scanner/${category.name}")
                                        }
                                    )
                                }

                                // 3. Category Device Scanner Screen (smarthome, periferal, Kamera, IoT, Printer)
                                composable(
                                    route = "scanner/{category}",
                                    arguments = listOf(
                                        navArgument("category") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    val categoryStr = backStackEntry.arguments?.getString("category")
                                    val category = DeviceCategory.fromName(categoryStr)

                                    CategoryScannerScreen(
                                        category = category,
                                        viewModel = viewModel,
                                        onBack = { navController.popBackStack() },
                                        onOpenSmartHomeRemote = { device ->
                                            val encodedName = Uri.encode(device.name ?: "Smart Home")
                                            navController.navigate("remote/$encodedName")
                                        }
                                    )
                                }

                                // 4. Smart Home Remote Screen (TV, DVD, Kipas, AC, Smartbox, Proyektor)
                                composable(
                                    route = "remote/{deviceName}",
                                    arguments = listOf(
                                        navArgument("deviceName") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    val deviceName = Uri.decode(
                                        backStackEntry.arguments?.getString("deviceName") ?: "Smart Home"
                                    )
                                    val initialRemote = SmartHomeRemoteType.detectFromDeviceName(deviceName)

                                    SmartHomeRemoteScreen(
                                        deviceName = deviceName,
                                        initialRemoteType = initialRemote,
                                        onBack = { navController.popBackStack() },
                                        onSendCommand = { cmd ->
                                            viewModel.sendRemoteCommand(cmd)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Startup Splash Animation: Black Background -> Logo Pop-In -> Fade-Out -> Main App
                    if (showSplash) {
                        SplashScreenOverlay(
                            onSplashFinished = { showSplash = false }
                        )
                    }
                }
            }
        }
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (gamepadInputManager.handleGenericMotionEvent(event)) {
            return true
        }
        return super.dispatchGenericMotionEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (gamepadInputManager.handleKeyEvent(event)) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}

@Composable
private fun SplashScreenOverlay(
    onSplashFinished: () -> Unit
) {
    val logoScale = remember { Animatable(0.25f) }
    val logoAlpha = remember { Animatable(0f) }
    val overlayAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 1. Pop-In Animation (scale up with subtle bounce + fade in)
        val scaleJob = launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        val alphaJob = launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        }
        scaleJob.join()
        alphaJob.join()

        // 2. Brief pause after pop-in finishes
        delay(450)

        // 3. Fade-Out Animation before entering the main app
        overlayAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(overlayAlpha.value)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale.value)
                .alpha(logoAlpha.value)
        ) {
            // Official App Launcher Logo (Blue rounded square with white Bluetooth symbol, no white background)
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0D47A1)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "BlueHub App Logo",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "BlueHub",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
