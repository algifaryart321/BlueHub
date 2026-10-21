package com.bluehub.app.core.service;

import com.bluehub.app.core.bluetooth.ble.BlueHubBleManager;
import com.bluehub.app.core.bluetooth.classic.BluetoothClassicManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BluetoothForegroundService_MembersInjector implements MembersInjector<BluetoothForegroundService> {
  private final Provider<BlueHubBleManager> bleManagerProvider;

  private final Provider<BluetoothClassicManager> classicManagerProvider;

  public BluetoothForegroundService_MembersInjector(Provider<BlueHubBleManager> bleManagerProvider,
      Provider<BluetoothClassicManager> classicManagerProvider) {
    this.bleManagerProvider = bleManagerProvider;
    this.classicManagerProvider = classicManagerProvider;
  }

  public static MembersInjector<BluetoothForegroundService> create(
      Provider<BlueHubBleManager> bleManagerProvider,
      Provider<BluetoothClassicManager> classicManagerProvider) {
    return new BluetoothForegroundService_MembersInjector(bleManagerProvider, classicManagerProvider);
  }

  @Override
  public void injectMembers(BluetoothForegroundService instance) {
    injectBleManager(instance, bleManagerProvider.get());
    injectClassicManager(instance, classicManagerProvider.get());
  }

  @InjectedFieldSignature("com.bluehub.app.core.service.BluetoothForegroundService.bleManager")
  public static void injectBleManager(BluetoothForegroundService instance,
      BlueHubBleManager bleManager) {
    instance.bleManager = bleManager;
  }

  @InjectedFieldSignature("com.bluehub.app.core.service.BluetoothForegroundService.classicManager")
  public static void injectClassicManager(BluetoothForegroundService instance,
      BluetoothClassicManager classicManager) {
    instance.classicManager = classicManager;
  }
}
