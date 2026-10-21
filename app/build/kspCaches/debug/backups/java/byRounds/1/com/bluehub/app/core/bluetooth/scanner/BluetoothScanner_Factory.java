package com.bluehub.app.core.bluetooth.scanner;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
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
public final class BluetoothScanner_Factory implements Factory<BluetoothScanner> {
  private final Provider<Context> contextProvider;

  private final Provider<BluetoothAdapter> bluetoothAdapterProvider;

  public BluetoothScanner_Factory(Provider<Context> contextProvider,
      Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    this.contextProvider = contextProvider;
    this.bluetoothAdapterProvider = bluetoothAdapterProvider;
  }

  @Override
  public BluetoothScanner get() {
    return newInstance(contextProvider.get(), bluetoothAdapterProvider.get());
  }

  public static BluetoothScanner_Factory create(Provider<Context> contextProvider,
      Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    return new BluetoothScanner_Factory(contextProvider, bluetoothAdapterProvider);
  }

  public static BluetoothScanner newInstance(Context context, BluetoothAdapter bluetoothAdapter) {
    return new BluetoothScanner(context, bluetoothAdapter);
  }
}
