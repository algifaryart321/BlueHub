package com.bluehub.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BlueHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Do NOT start Foreground Service here because in Android 14+ (API 34/35)
        // starting FGS with type connectedDevice requires runtime permissions
        // (BLUETOOTH_CONNECT / BLUETOOTH_SCAN) which have not yet been granted on app launch.
    }
}
