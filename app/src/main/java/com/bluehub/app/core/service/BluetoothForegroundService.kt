package com.bluehub.app.core.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bluehub.app.MainActivity
import com.bluehub.app.R
import com.bluehub.app.core.bluetooth.ble.BlueHubBleManager
import com.bluehub.app.core.bluetooth.classic.BluetoothClassicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "bluehub_connected_device_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        fun startService(context: Context) {
            // Only start if we have the needed Bluetooth permissions on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val hasConnect = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasConnect) {
                    return
                }
            }

            val intent = Intent(context, BluetoothForegroundService::class.java).apply {
                action = ACTION_START
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        }

        fun stopService(context: Context) {
            val intent = Intent(context, BluetoothForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {}
        }
    }

    @Inject
    lateinit var bleManager: BlueHubBleManager

    @Inject
    lateinit var classicManager: BluetoothClassicManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val started = startForegroundWithNotification("BlueHub aktif - Memantau perangkat Bluetooth")
                if (started) {
                    observeConnections()
                } else {
                    stopSelf()
                }
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun observeConnections() {
        serviceScope.launch {
            combine(
                bleManager.isConnected,
                classicManager.isConnected,
                classicManager.connectedDeviceName
            ) { bleConnected, classicConnected, classicName ->
                when {
                    bleConnected && classicConnected -> "Terhubung: BLE Device & $classicName"
                    bleConnected -> "Terhubung ke BLE IoT Device"
                    classicConnected -> "Terhubung ke $classicName"
                    else -> "BlueHub Aktif: Menunggu koneksi perangkat"
                }
            }.collect { statusText ->
                updateNotification(statusText)
            }
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundWithNotification(contentText: String): Boolean {
        val notification = buildNotification(contentText)
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun updateNotification(contentText: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(contentText))
    }

    private fun buildNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BlueHub IoT & Controller")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BlueHub Bluetooth Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Menjaga koneksi Bluetooth dan telemetri IoT tetap aktif di latar belakang"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
