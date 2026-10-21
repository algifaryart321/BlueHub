package com.bluehub.app.data.repository;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import com.bluehub.app.core.bluetooth.ble.BlueHubBleManager;
import com.bluehub.app.core.bluetooth.classic.BluetoothClassicManager;
import com.bluehub.app.core.bluetooth.scanner.BluetoothScanner;
import com.bluehub.app.data.local.db.DeviceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class BluetoothRepositoryImpl_Factory implements Factory<BluetoothRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<BluetoothScanner> scannerProvider;

  private final Provider<BlueHubBleManager> bleManagerProvider;

  private final Provider<BluetoothClassicManager> classicManagerProvider;

  private final Provider<DeviceDao> deviceDaoProvider;

  private final Provider<BluetoothAdapter> bluetoothAdapterProvider;

  public BluetoothRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<BluetoothScanner> scannerProvider, Provider<BlueHubBleManager> bleManagerProvider,
      Provider<BluetoothClassicManager> classicManagerProvider,
      Provider<DeviceDao> deviceDaoProvider, Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    this.contextProvider = contextProvider;
    this.scannerProvider = scannerProvider;
    this.bleManagerProvider = bleManagerProvider;
    this.classicManagerProvider = classicManagerProvider;
    this.deviceDaoProvider = deviceDaoProvider;
    this.bluetoothAdapterProvider = bluetoothAdapterProvider;
  }

  @Override
  public BluetoothRepositoryImpl get() {
    return newInstance(contextProvider.get(), scannerProvider.get(), bleManagerProvider.get(), classicManagerProvider.get(), deviceDaoProvider.get(), bluetoothAdapterProvider.get());
  }

  public static BluetoothRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<BluetoothScanner> scannerProvider, Provider<BlueHubBleManager> bleManagerProvider,
      Provider<BluetoothClassicManager> classicManagerProvider,
      Provider<DeviceDao> deviceDaoProvider, Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    return new BluetoothRepositoryImpl_Factory(contextProvider, scannerProvider, bleManagerProvider, classicManagerProvider, deviceDaoProvider, bluetoothAdapterProvider);
  }

  public static BluetoothRepositoryImpl newInstance(Context context, BluetoothScanner scanner,
      BlueHubBleManager bleManager, BluetoothClassicManager classicManager, DeviceDao deviceDao,
      BluetoothAdapter bluetoothAdapter) {
    return new BluetoothRepositoryImpl(context, scanner, bleManager, classicManager, deviceDao, bluetoothAdapter);
  }
}
