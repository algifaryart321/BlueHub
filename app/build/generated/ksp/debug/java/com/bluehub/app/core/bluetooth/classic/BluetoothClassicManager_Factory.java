package com.bluehub.app.core.bluetooth.classic;

import android.bluetooth.BluetoothAdapter;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class BluetoothClassicManager_Factory implements Factory<BluetoothClassicManager> {
  private final Provider<BluetoothAdapter> bluetoothAdapterProvider;

  public BluetoothClassicManager_Factory(Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    this.bluetoothAdapterProvider = bluetoothAdapterProvider;
  }

  @Override
  public BluetoothClassicManager get() {
    return newInstance(bluetoothAdapterProvider.get());
  }

  public static BluetoothClassicManager_Factory create(
      Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    return new BluetoothClassicManager_Factory(bluetoothAdapterProvider);
  }

  public static BluetoothClassicManager newInstance(BluetoothAdapter bluetoothAdapter) {
    return new BluetoothClassicManager(bluetoothAdapter);
  }
}
