package com.bluehub.app.core.service;

import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.internal.GeneratedEntryPoint;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = BluetoothForegroundService.class
)
@GeneratedEntryPoint
@InstallIn(ServiceComponent.class)
@Generated("dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator")
public interface BluetoothForegroundService_GeneratedInjector {
  void injectBluetoothForegroundService(BluetoothForegroundService bluetoothForegroundService);
}
